#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/slab.h>
#include "http.h"

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Martynov Alexander");
MODULE_VERSION("0.01");

struct entries {
  size_t entries_count;
  struct entry {
    unsigned char entry_type;
    ino_t ino;
    char name[256];
  } entries[16];
};

int networkfs_iterate(struct file *filp, struct dir_context *ctx) {
  struct dentry *dentry;
  struct inode *inode;
  unsigned long offset;
  int stored;
  ino_t ino;
  dentry = filp->f_path.dentry;
  inode = dentry->d_inode;
  offset = filp->f_pos;
  stored = 0;
  ino = inode->i_ino;
  char ino_node[10];
  snprintf(ino_node, 10, "%lu", ino);
  struct entries *elements;
  if ((elements = kzalloc(sizeof(struct entries), GFP_KERNEL)) == NULL) {
    return 0;
  }
  int status =
      networkfs_http_call(inode->i_sb->s_fs_info, "list", (char *)elements,
                          sizeof(struct entries), 1, "inode", ino_node);
  if (status != 0) {
    kfree(elements);
    return stored;
  }
  size_t it = 0;
  while (true) {
    if (offset == 0) {
      dir_emit_dot(filp, ctx);
    } else if (offset == 1) {
      dir_emit_dotdot(filp, ctx);
    } else if (offset < elements->entries_count + 2) {
      dir_emit(ctx, elements->entries[it].name,
               strlen(elements->entries[it].name), elements->entries[it].ino,
               elements->entries[it].entry_type);
      it++;
    } else {
      break;
    }
    stored++;
    offset++;
    ctx->pos = offset;
  }
  kfree(elements);
  return stored;
}

struct content {
  u64 content_length;
  char content[512];
};

ssize_t networkfs_read(struct file *filp, char *buffer, size_t len,
                       loff_t *offset) {
  struct dentry *dentry;
  struct inode *inode;
  ino_t ino;
  dentry = filp->f_path.dentry;
  inode = dentry->d_inode;
  ino = inode->i_ino;
  char ino_node[10];
  snprintf(ino_node, 10, "%lu", ino);
  struct content *content;
  if ((content = kzalloc(sizeof(struct content), GFP_KERNEL)) == NULL) {
    return 0;
  }
  int status =
      networkfs_http_call(inode->i_sb->s_fs_info, "read", (char *)content,
                          sizeof(struct content), 1, "inode", ino_node);
  char *text;
  if ((text = kzalloc(content->content_length, GFP_KERNEL)) == NULL) {
    return 0;
  }
  strcpy(text, content->content);
  copy_to_user(buffer, text, content->content_length);
  offset += len;
  kfree(content);
  return len;
}

char *parse_symbols(const char *str) {
  char *new_str;
  if ((new_str = kzalloc(3 * strlen(str), GFP_KERNEL)) == NULL) {
    return 0;
  }
  for (int i = 0, j = 0; i < strlen(str); i++) {
    printk(KERN_INFO "%c\n", str[i]);
    new_str[j++] = '%';
    new_str[j++] = str[i] / 16 + '0';
    if (str[i] % 16 >= 10) {
      new_str[j++] = (str[i] % 16 - 10) + 'A';
    } else {
      new_str[j++] = str[i] % 16 + '0';
    }
  }
  return new_str;
}

ssize_t networkfs_write(struct file *filp, const char *buffer, size_t len,
                        loff_t *offset) {
  struct dentry *dentry;
  struct inode *inode;
  ino_t ino;
  dentry = filp->f_path.dentry;
  inode = dentry->d_inode;
  ino = inode->i_ino;
  char ino_node[10];
  snprintf(ino_node, 10, "%lu", ino);
  ino_t write_ino;
  char *text;
  if ((text = kzalloc(len, GFP_KERNEL)) == NULL) {
    return 0;
  }
  copy_from_user(text, buffer, len);
  int status = networkfs_http_call(
      inode->i_sb->s_fs_info, "write", (char *)&write_ino, sizeof(write_ino), 2,
      "inode", ino_node, "content", parse_symbols(text));
  offset += len;
  kfree(text);
  return len;
}

struct file_operations networkfs_dir_ops = {
    .iterate = networkfs_iterate,
    .read = networkfs_read,
    .write = networkfs_write,
};

struct entry_info {
  unsigned char entry_type;
  ino_t ino;
};

struct inode *networkfs_get_inode(struct super_block *, const struct inode *,
                                  umode_t, int);

struct dentry *networkfs_lookup(struct inode *parent_inode,
                                struct dentry *child_dentry,
                                unsigned int flag) {
  ino_t root;
  struct inode *inode;
  const char *name = child_dentry->d_name.name;
  root = parent_inode->i_ino;
  char root_node[10];
  snprintf(root_node, 10, "%lu", root);
  struct entry_info info;
  int status = networkfs_http_call(parent_inode->i_sb->s_fs_info, "lookup",
                                   (char *)&info, sizeof(info), 2, "parent",
                                   root_node, "name", name);
  if (status == 0) {
    inode = networkfs_get_inode(parent_inode->i_sb, NULL,
                                (info.entry_type == 4 ? S_IFDIR : S_IFREG),
                                info.ino);
    d_add(child_dentry, inode);
  }
  return NULL;
}

int networkfs_create(struct user_namespace *ns, struct inode *parent_inode,
                     struct dentry *child_dentry, umode_t mode, bool b) {
  ino_t root;
  struct inode *inode;
  const char *name = child_dentry->d_name.name;
  root = parent_inode->i_ino;
  ino_t newobj;
  char root_node[10];
  snprintf(root_node, 10, "%lu", root);
  int status = networkfs_http_call(
      parent_inode->i_sb->s_fs_info, "create", (char *)&newobj, sizeof(newobj),
      3, "parent", root_node, "name", parse_symbols(name), "type", "file");
  if (status == 0) {
    inode = networkfs_get_inode(parent_inode->i_sb, NULL, S_IFREG | S_IRWXUGO,
                                newobj);
    d_add(child_dentry, inode);
  }
  return status;
}

int networkfs_unlink(struct inode *parent_inode, struct dentry *child_dentry) {
  const char *name = child_dentry->d_name.name;
  ino_t root;
  root = parent_inode->i_ino;
  char root_node[10];
  snprintf(root_node, 10, "%lu", root);
  ino_t rm_ino;
  return networkfs_http_call(parent_inode->i_sb->s_fs_info, "unlink",
                             (char *)&rm_ino, sizeof(rm_ino), 2, "parent",
                             root_node, "name", name);
}

int networkfs_mkdir(struct user_namespace *ns, struct inode *parent_inode,
                    struct dentry *child_dentry, umode_t mode) {
  ino_t root;
  struct inode *inode;
  const char *name = child_dentry->d_name.name;
  root = parent_inode->i_ino;
  ino_t newobj;
  char root_node[10];
  snprintf(root_node, 10, "%lu", root);
  int status = networkfs_http_call(
      parent_inode->i_sb->s_fs_info, "create", (char *)&newobj, sizeof(newobj),
      3, "parent", root_node, "name", parse_symbols(name), "type", "directory");
  if (status == 0) {
    inode = networkfs_get_inode(parent_inode->i_sb, NULL, S_IFDIR | S_IRWXUGO,
                                newobj);
    d_add(child_dentry, inode);
  }
  return status;
}

int networkfs_rmdir(struct inode *parent_inode, struct dentry *child_dentry) {
  const char *name = child_dentry->d_name.name;
  ino_t root;
  root = parent_inode->i_ino;
  char root_node[10];
  snprintf(root_node, 10, "%lu", root);
  ino_t rm_ino;
  return networkfs_http_call(parent_inode->i_sb->s_fs_info, "rmdir",
                             (char *)&rm_ino, sizeof(rm_ino), 2, "parent",
                             root_node, "name", name);
}

struct inode_operations networkfs_inode_ops = {.lookup = networkfs_lookup,
                                               .create = networkfs_create,
                                               .unlink = networkfs_unlink,
                                               .mkdir = networkfs_mkdir,
                                               .rmdir = networkfs_rmdir};

struct inode *networkfs_get_inode(struct super_block *sb,
                                  const struct inode *dir, umode_t mode,
                                  int i_ino) {
  struct inode *inode;
  inode = new_inode(sb);
  inode->i_op = &networkfs_inode_ops;
  inode->i_fop = &networkfs_dir_ops;
  inode->i_ino = i_ino;
  if (inode != NULL) {
    mode |= 511;
    inode_init_owner(&init_user_ns, inode, dir, mode);
  }
  return inode;
}

int networkfs_fill_super(struct super_block *sb, void *data, int silent) {
  struct inode *inode;
  inode = networkfs_get_inode(sb, NULL, S_IFDIR, 1000);
  sb->s_root = d_make_root(inode);
  char *str;
  if ((str = kzalloc(37, GFP_KERNEL)) == NULL) {
    return -ENOMEM;
  }
  strcpy(str, (char *)data);
  sb->s_fs_info = str;
  if (sb->s_root == NULL) {
    return -ENOMEM;
  }
  printk(KERN_INFO "return 0\n");
  return 0;
}

struct dentry *networkfs_mount(struct file_system_type *fs_type, int flags,
                               const char *token, void *data) {
  struct dentry *ret;
  ret = mount_nodev(fs_type, flags, (void *)token, networkfs_fill_super);
  if (ret == NULL) {
    printk(KERN_ERR "Can't mount file system");
  } else {
    printk(KERN_INFO "Mounted successfuly");
  }
  return ret;
}

void networkfs_kill_sb(struct super_block *sb) {
  kfree(sb->s_fs_info);
  printk(KERN_INFO
         "networkfs super block is destroyed. Unmount successfully.\n");
}

struct file_system_type networkfs_fs_type = {.name = "networkfs",
                                             .mount = networkfs_mount,
                                             .kill_sb = networkfs_kill_sb};

int networkfs_init(void) {
  register_filesystem(&networkfs_fs_type);
  printk(KERN_INFO "Hello, World!\n");
  return 0;
}

void networkfs_exit(void) {
  unregister_filesystem(&networkfs_fs_type);
  printk(KERN_INFO "Goodbye!\n");
}

module_init(networkfs_init);
module_exit(networkfs_exit);

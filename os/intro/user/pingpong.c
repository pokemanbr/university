#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"

int main(int argc, char* argv[]) {
  int fd1[2], fd2[2];
  if (pipe(fd1) == -1 || pipe(fd2) == -1) {
    exit(0);
  }

  int pid = fork();
  if (pid < 0) {
    exit(0);
  } else if (pid > 0) {
    close(fd1[0]);
    write(fd1[1], "ping", strlen("ping") + 1);
    close(fd1[1]);

    close(fd2[1]);
    char buf[5];
    read(fd2[0], buf, 5);
    close(fd2[0]);
    printf("%d: got %s\n", getpid(), buf);
  } else {
    close(fd1[1]);
    char buf[5];
    read(fd1[0], buf, 5);
    close(fd1[0]);
    printf("%d: got %s\n", getpid(), buf);

    close(fd2[0]);
    write(fd2[1], "pong", strlen("pong") + 1);
    close(fd2[1]);
  }

  exit(0);
}

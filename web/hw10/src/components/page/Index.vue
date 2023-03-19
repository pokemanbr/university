<template>
    <div>
        <Post v-for="post in viewPosts" :post="post" :user="users[post.userId]" :countComments="countComments(post.id)"
              :key="post.id"/>
    </div>
</template>

<script>
import Post from "./Post";

export default {
    name: "Index",
    components: {Post},
    props: ["posts", "users", "comments"],
    computed: {
        viewPosts: function () {
            return Object.values(this.posts).sort((a, b) => b.id - a.id);
        }
    },
    methods: {
        countComments: function (id) {
            return Object.values(this.comments).filter(a => a.postId === id).length;
        }
    }
}
</script>

<style scoped>

</style>

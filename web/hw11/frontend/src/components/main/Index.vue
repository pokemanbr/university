<template>
    <div>
        <Post v-for="post in viewPosts" :post="post" :key="post.id"/>
    </div>
</template>

<script>
import Post from "../page/Post";

export default {
    name: "Index",
    components: {Post},
    props: ["posts"],
    computed: {
        viewPosts: function () {
            return Object.values(this.posts).sort((a, b) => b.creationTime - a.creationTime);
        }
    }
}
</script>

<style scoped>

</style>

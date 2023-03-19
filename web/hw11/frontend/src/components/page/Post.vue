<template>
    <article>
        <div class="title">
            {{ post.title }}
        </div>
        <div class="information">By {{ post.user.login }}</div>
        <div class="body">{{ post.text }}</div>
    </article>
</template>


<script>
export default {
    name: "Post",
    props: ["post"]
}
</script>

<style scoped>

</style>
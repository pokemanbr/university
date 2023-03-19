<template>
    <article>
        <div class="title">
            {{ post.title }}
        </div>
        <div class="information">By {{ user.login }}</div>
        <div class="body">{{ post.text }}</div>
        <ul class="attachment">
        </ul>
        <div class="footer">
            <div class="right">
                Comments
                <a href="#">{{ countComments }}</a>
            </div>
        </div>
    </article>
</template>


<script>
export default {
    name: "Post",
    props: ["post", "user", "countComments"]
}
</script>

<style scoped>

</style>
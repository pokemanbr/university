<template>
    <footer>
      Posts: {{ numberPosts }} | Users: {{ numberUsers }}
    </footer>
</template>

<script>
export default {
    name: "Footer",
    props: ["numberUsers", "numberPosts"]
}
</script>

<style scoped>

</style>
<template>
    <div id="app">
        <Header :userId="userId" :users="users"/>
        <Middle :posts="posts" :users="users" :comments="comments"/>
        <Footer :numberPosts="Object.values(posts).length" :numberUsers="Object.values(users).length"/>
    </div>
</template>

<script>
import Header from "./components/Header";
import Middle from "./components/Middle";
import Footer from "./components/Footer";

export default {
    name: 'App',
    components: {
        Footer,
        Middle,
        Header
    },
    data: function () {
        return this.$root.$data;
    },
    beforeCreate() {
        this.$root.$on("onEnter", (login, password) => {
            if (password === "") {
                this.$root.$emit("onEnterValidationError", "Password is required");
                return;
            }

            const users = Object.values(this.users).filter(u => u.login === login);
            if (users.length === 0) {
                this.$root.$emit("onEnterValidationError", "No such user");
            } else {
                this.userId = users[0].id;
                this.$root.$emit("onChangePage", "Index");
            }
        });

        // eslint-disable-next-line no-unused-vars
        this.$root.$on("onRegister", (name, login, password) => {
            if (!this.userId) {
                if (!name || name.length < 1 || name.length > 32) {
                    this.$root.$emit("onRegisterValidationError", "The size of the name should be from 1 to 32 chars");
                } else if (!login || login.length < 3) {
                    this.$root.$emit("onRegisterValidationError", "Login size should be at least 3");
                } else if (login.length > 16) {
                    this.$root.$emit("onRegisterValidationError", "Login size shouldn't be more than 3");
                } else if (!/^[a-z]*$/.test(login)) {
                    this.$root.$emit("onRegisterValidationError", "Login should have only lowercase latin letters");
                } else if (Object.values(this.users).filter(u => u.login === login).length !== 0) {
                    this.$root.$emit("onRegisterValidationError", "Login already exists");
                } else {
                    const id = Math.max(...Object.keys(this.users)) + 1;
                    const users = Object.values(this.users);
                    let userId = users[0].id + 1;
                    for (let i = 1; i < users.length; i++) {
                        userId = Math.max(userId, users[i].id + 1);
                    }
                    this.$root.$set(this.users, id, {
                        id: userId, login, name, admin: false
                    });
                    this.$root.$emit("onChangePage", "Enter");
                }
            } else {
                this.$root.$emit("onRegisterValidationError", "You can't register when you're already log in");
            }
        });

        this.$root.$on("onLogout", () => this.userId = null);

        this.$root.$on("onWritePost", (title, text) => {
            if (this.userId) {
                if (!title || title.length < 5) {
                    this.$root.$emit("onWritePostValidationError", "Title is too short");
                } else if (!text || text.length < 10) {
                    this.$root.$emit("onWritePostValidationError", "Text is too short");
                } else {
                    const id = Math.max(...Object.keys(this.posts)) + 1;
                    this.$root.$set(this.posts, id, {
                        id, title, text, userId: this.userId
                    });
                }
            } else {
                this.$root.$emit("onWritePostValidationError", "No access");
            }
        });

        this.$root.$on("onEditPost", (id, text) => {
            if (this.userId) {
                if (!id) {
                    this.$root.$emit("onEditPostValidationError", "ID is invalid");
                } else if (!text || text.length < 10) {
                    this.$root.$emit("onEditPostValidationError", "Text is too short");
                } else {
                    let posts = Object.values(this.posts).filter(p => p.id === parseInt(id));
                    if (posts.length) {
                        posts.forEach((item) => {
                            item.text = text;
                        });
                    } else {
                        this.$root.$emit("onEditPostValidationError", "No such post");
                    }
                }
            } else {
                this.$root.$emit("onEditPostValidationError", "No access");
            }
        });
    }
}
</script>

<style>
#app {

}
</style>

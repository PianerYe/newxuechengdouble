<template>
  <div></div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'

// @Component({})
export default {
  name: 'app',
    components: {
    },
    created: function () {
    // @ts-ignore
      this.$router.push('/organization/course-list')
    }
}
</script>

<style lang="scss" scoped>

</style>

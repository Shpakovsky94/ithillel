<template>
  <div>
    <div class="tabs">
      <div
        class="tab"
        v-for="(tab, index) in tabs"
        :key="index"
        @click="selectTab(index)"
        :class="{ active: activeTab === index }"
      >
        {{ tab }}
      </div>
    </div>
    <div class="tab-content">
      <component :is="currentComponent"></component>
    </div>
  </div>
</template>

<script>
import Welcome from './components/Welcome.vue';
import GetAllEmojis from './components/GetAllEmojis.vue';

export default {
  data() {
    return {
      tabs: [
        'Home',
        'Get All Emojis',
      ],
      activeTab: 0
    };
  },
  computed: {
    currentComponent() {
      switch (this.activeTab) {
        case 0:
          return Welcome;
        case 1:
          return GetAllEmojis;
      }
    }
  },
  methods: {
    selectTab(index) {
      this.activeTab = index;
    }
  }
};
</script>

<style>

.tabs {
  position: fixed; /* Set the position to fixed */
  top: 20px; /* Distance from top */
  display: flex;
  justify-content: flex-start;
  margin-bottom: 20px;
}

.tab {
  padding: 10px;
  cursor: pointer;
  background-color: #eee;
  border: 1px solid #ccc;
  color: #333; /* Text color */
  border-radius: 5px;
  margin-right: 10px;
}




/* Added style for text color */
.tab-content p {
  margin: 20px;
  color: #333;
}

/* Added style for table cell background */
.emoji-table td {
  background-color: #f9f9f9;
}
</style>

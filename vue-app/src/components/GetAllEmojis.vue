<template>
  <div>
    <div class="table-container">
      <table class="emoji-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Category</th>
            <th>Group</th>
            <th>Emoji</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(emoji) in paginatedEmojis" :key="emoji.id">
            <td>{{ emoji.id }}</td>
            <td>{{ emoji.name }}</td>
            <td>{{ emoji.category }}</td>
            <td>{{ emoji.group }}</td>
            <td>
              <span v-html="convertToEmoji(emoji.htmlCode)" class="emoji-icon"></span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <button @click="previousPage" :disabled="currentPage === 1">Previous</button>
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
    </div>
  </div>
</template>

<style scoped>
.table-container {
  align-content: center;
  max-width: 800px;
  margin: 10;
}

.emoji-table {
  width: 100%;
  border-collapse: collapse;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.emoji-table th,
.emoji-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
  color: #333; /* Text color */
}

.emoji-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.emoji-table td:last-child {
  font-size: 20px;
  padding-left: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.pagination button {
  padding: 5px 20px;
  border: 1px solid #ddd;
  background-color: #f2f2f2;
  color: #333;
  cursor: pointer;
  border-radius: 4px;
  margin-right: 10px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination .page-info {
  margin-left: 10px;
}
</style>


<script>
import EmojiService from '../EmojiService';

export default {
  data() {
    return {
      emojis: [],
      currentPage: 1,
      pageSize: 7,
      totalPages: 0
    };
  },
  mounted() {
    this.getAllEmojis();
  },
  computed: {
    paginatedEmojis() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      return this.emojis.slice(startIndex, endIndex);
    }
  },
  methods: {
    getAllEmojis() {
      EmojiService.getAllEmojis()
        .then(response => {
          console.log(response.data);
          this.emojis = response.data;
          this.totalPages = Math.ceil(this.emojis.length / this.pageSize);
        })
        .catch(error => {
          console.error(error);
        });
    },
    convertToEmoji(htmlCode) {
      try {
        const emojiCode = htmlCode.match(/&#(\d+);/);
        if (emojiCode && emojiCode[1]) {
          const emoji = String.fromCodePoint(parseInt(emojiCode[1]));
          return emoji;
        }
      } catch (error) {
        console.error('Error converting HTML code to emoji:', error);
      }
      return ''; // Return empty string or any other fallback option
    },
    previousPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    }
  }
};
</script>

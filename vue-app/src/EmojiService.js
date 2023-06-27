import axios from 'axios';

const API_BASE_URL = 'http://api.lafcard.site/';
//const API_BASE_URL = 'http://localhost:8081/';

export default {
  createEmoji(emoji) {
    return axios.post(API_BASE_URL, emoji);
  },

  getAllEmojis() {
    return axios.get(API_BASE_URL + 'all');
  },

  getEmojiById(id) {
    return axios.get(API_BASE_URL + id);
  },

  updateEmoji(id, updatedEmoji) {
    return axios.put(API_BASE_URL + id, updatedEmoji);
  },

  deleteEmoji(id) {
    return axios.delete(API_BASE_URL + id);
  }
};


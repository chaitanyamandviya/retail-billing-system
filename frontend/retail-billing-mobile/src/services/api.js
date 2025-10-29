import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

// Base URL - Change this to your computer's IP when testing on phone
const BASE_URL = 'http://localhost:8080/api';
// For testing on phone, use: http://YOUR_COMPUTER_IP:8080/api

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Authentication
export const authAPI = {
  login: async (username, password) => {
    const response = await api.post('/auth/login', { username, password });
    if (response.data.token) {
      await AsyncStorage.setItem('jwt_token', response.data.token);
      await AsyncStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  logout: async () => {
    await AsyncStorage.removeItem('jwt_token');
    await AsyncStorage.removeItem('user');
  },

  getCurrentUser: async () => {
    const response = await api.get('/auth/me');
    return response.data;
  },
};

// Products
export const productsAPI = {
  getSuggestions: async (query) => {
    const response = await api.get(`/products/suggestions?query=${query}`);
    return response.data;
  },

  getAll: async () => {
    const response = await api.get('/products');
    return response.data;
  },
};

// Bills
export const billsAPI = {
  create: async (billData) => {
    const response = await api.post('/bills', billData);
    return response.data;
  },

  getTodaysBills: async () => {
    const response = await api.get('/bills/today');
    return response.data;
  },

  getById: async (billId) => {
    const response = await api.get(`/bills/${billId}`);
    return response.data;
  },
};

// Reports
export const reportsAPI = {
  getTodaysSummary: async () => {
    const response = await api.get('/reports/today');
    return response.data;
  },
};

export default api;

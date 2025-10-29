import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  RefreshControl,
} from 'react-native';
import { useAuth } from '../context/AuthContext';
import { reportsAPI } from '../services/api';

export default function HomeScreen({ navigation }) {
  const { user, logout } = useAuth();
  const [summary, setSummary] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadSummary();
  }, []);

  const loadSummary = async () => {
    try {
      const data = await reportsAPI.getTodaysSummary();
      setSummary(data);
    } catch (error) {
      console.error('Error loading summary:', error);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadSummary();
    setRefreshing(false);
  };

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }
    >
      <View style={styles.header}>
        <Text style={styles.welcome}>Welcome, {user?.fullName}!</Text>
        <Text style={styles.role}>{user?.role}</Text>
      </View>

      {summary && (
        <View style={styles.summaryCard}>
          <Text style={styles.cardTitle}>Today's Summary</Text>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Total Bills:</Text>
            <Text style={styles.summaryValue}>{summary.totalBills}</Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Total Sales:</Text>
            <Text style={styles.summaryValue}>₹{summary.totalSales?.toFixed(2)}</Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Discounts Given:</Text>
            <Text style={styles.summaryValue}>₹{summary.totalDiscounts?.toFixed(2)}</Text>
          </View>
        </View>
      )}

      <View style={styles.actions}>
        <TouchableOpacity
          style={[styles.actionButton, styles.primaryButton]}
          onPress={() => navigation.navigate('CreateBill')}
        >
          <Text style={styles.actionButtonText}>📝 Create New Bill</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.actionButton, styles.secondaryButton]}
          onPress={() => navigation.navigate('TodaysBills')}
        >
          <Text style={styles.actionButtonText}>📋 Today's Bills</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.actionButton, styles.logoutButton]}
          onPress={logout}
        >
          <Text style={styles.actionButtonText}>🚪 Logout</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#3b5998',
    padding: 20,
    paddingTop: 10,
  },
  welcome: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'white',
  },
  role: {
    fontSize: 14,
    color: '#ddd',
    marginTop: 5,
  },
  summaryCard: {
    backgroundColor: 'white',
    margin: 15,
    padding: 20,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 3,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  summaryLabel: {
    fontSize: 16,
    color: '#666',
  },
  summaryValue: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  actions: {
    padding: 15,
  },
  actionButton: {
    padding: 20,
    borderRadius: 10,
    marginBottom: 15,
    alignItems: 'center',
  },
  primaryButton: {
    backgroundColor: '#4CAF50',
  },
  secondaryButton: {
    backgroundColor: '#2196F3',
  },
  logoutButton: {
    backgroundColor: '#f44336',
  },
  actionButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});

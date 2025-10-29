import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  RefreshControl,
  ActivityIndicator,
} from 'react-native';
import { billsAPI } from '../services/api';

export default function TodaysBillsScreen() {
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadBills();
  }, []);

  const loadBills = async () => {
    try {
      const data = await billsAPI.getTodaysBills();
      setBills(data);
    } catch (error) {
      console.error('Error loading bills:', error);
    } finally {
      setLoading(false);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadBills();
    setRefreshing(false);
  };

  const renderBillItem = ({ item }) => (
    <View style={styles.billCard}>
      <View style={styles.billHeader}>
        <Text style={styles.billNumber}>{item.billNumber}</Text>
        <Text style={styles.billAmount}>₹{item.totalAmount.toFixed(2)}</Text>
      </View>

      {item.customerName && (
        <Text style={styles.customerName}>👤 {item.customerName}</Text>
      )}

      <View style={styles.billDetails}>
        <Text style={styles.detailText}>
          Items: {item.items.length} • {item.paymentMethod}
        </Text>
        <Text style={styles.detailText}>
          {new Date(item.createdAt).toLocaleTimeString('en-IN', {
            hour: '2-digit',
            minute: '2-digit',
          })}
        </Text>
      </View>

      <View style={styles.itemsList}>
        {item.items.map((billItem, index) => (
          <Text key={index} style={styles.itemText}>
            • {billItem.productName} ({billItem.quantity}x) - ₹{billItem.totalPrice.toFixed(2)}
          </Text>
        ))}
      </View>

      {item.discountAmount > 0 && (
        <Text style={styles.discount}>
          Discount: -₹{item.discountAmount.toFixed(2)}
        </Text>
      )}
    </View>
  );

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#3b5998" />
      </View>
    );
  }

  if (bills.length === 0) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.emptyText}>No bills created today</Text>
        <Text style={styles.emptySubtext}>Create your first bill to get started!</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.summary}>
        <Text style={styles.summaryText}>
          Total Bills: {bills.length}
        </Text>
        <Text style={styles.summaryText}>
          Total: ₹
          {bills.reduce((sum, bill) => sum + bill.totalAmount, 0).toFixed(2)}
        </Text>
      </View>

      <FlatList
        data={bills}
        renderItem={renderBillItem}
        keyExtractor={(item) => item.billId.toString()}
        contentContainerStyle={styles.listContainer}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  summary: {
    backgroundColor: '#3b5998',
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 15,
  },
  summaryText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
  listContainer: {
    padding: 10,
  },
  billCard: {
    backgroundColor: 'white',
    borderRadius: 10,
    padding: 15,
    marginBottom: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  billHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  billNumber: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#3b5998',
  },
  billAmount: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#4CAF50',
  },
  customerName: {
    fontSize: 14,
    color: '#333',
    marginBottom: 8,
  },
  billDetails: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  detailText: {
    fontSize: 12,
    color: '#666',
  },
  itemsList: {
    marginTop: 5,
    paddingTop: 10,
    borderTopWidth: 1,
    borderTopColor: '#eee',
  },
  itemText: {
    fontSize: 13,
    color: '#555',
    marginBottom: 3,
  },
  discount: {
    fontSize: 12,
    color: '#f44336',
    marginTop: 5,
    fontWeight: 'bold',
  },
  emptyText: {
    fontSize: 18,
    color: '#666',
    marginBottom: 10,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
  },
});

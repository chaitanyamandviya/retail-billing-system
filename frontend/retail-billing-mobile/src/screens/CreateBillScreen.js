import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
  FlatList,
} from 'react-native';
import { useAuth } from '../context/AuthContext';
import { billsAPI, productsAPI } from '../services/api';

export default function CreateBillScreen({ navigation }) {
  const { user } = useAuth();
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [items, setItems] = useState([]);
  const [currentItem, setCurrentItem] = useState({
    productName: '',
    quantity: '',
    unitPrice: '',
  });
  const [suggestions, setSuggestions] = useState([]);
  const [manualDiscount, setManualDiscount] = useState('0');
  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  // Calculate totals
  const calculateTotals = () => {
    const subtotal = items.reduce((sum, item) => {
      return sum + (parseFloat(item.quantity) * parseFloat(item.unitPrice));
    }, 0);

    const fixedDiscount = subtotal * 0.10; // 10% fixed
    const manualDiscountAmount = parseFloat(manualDiscount) || 0;
    const totalDiscount = fixedDiscount + manualDiscountAmount;
    const total = Math.max(0, subtotal - totalDiscount);

    return { subtotal, fixedDiscount, manualDiscountAmount, totalDiscount, total };
  };

  const totals = calculateTotals();

  // Product name search with autocomplete
  const searchProducts = async (query) => {
    setCurrentItem({ ...currentItem, productName: query });

    if (query.length >= 2) {
      try {
        const results = await productsAPI.getSuggestions(query);
        setSuggestions(results);
      } catch (error) {
        console.error('Error searching products:', error);
        setSuggestions([]);
      }
    } else {
      setSuggestions([]);
    }
  };

  const selectSuggestion = (productName) => {
    setCurrentItem({ ...currentItem, productName });
    setSuggestions([]);
  };

  const addItem = () => {
    if (!currentItem.productName.trim()) {
      Alert.alert('Error', 'Please enter product name');
      return;
    }
    if (!currentItem.quantity || parseFloat(currentItem.quantity) <= 0) {
      Alert.alert('Error', 'Please enter valid quantity');
      return;
    }
    if (!currentItem.unitPrice || parseFloat(currentItem.unitPrice) < 0) {
      Alert.alert('Error', 'Please enter valid price');
      return;
    }

    const newItem = {
      productName: currentItem.productName.trim(),
      quantity: parseInt(currentItem.quantity),
      unitPrice: parseFloat(currentItem.unitPrice),
      totalPrice: parseInt(currentItem.quantity) * parseFloat(currentItem.unitPrice),
    };

    setItems([...items, newItem]);
    setCurrentItem({ productName: '', quantity: '', unitPrice: '' });
    setSuggestions([]);
  };

  const removeItem = (index) => {
    const newItems = items.filter((_, i) => i !== index);
    setItems(newItems);
  };

  const createBill = async () => {
    if (items.length === 0) {
      Alert.alert('Error', 'Please add at least one item');
      return;
    }

    setLoading(true);
    setSuccess(false);

    try {
      const billData = {
        userId: user.userId,
        customerName: customerName.trim() || null,
        customerPhone: customerPhone.trim() || null,
        items: items,
        manualDiscountAmount: parseFloat(manualDiscount) || 0,
        paymentMethod: paymentMethod,
      };

      const response = await billsAPI.create(billData);

      // Show success
      setSuccess(true);

      // Clear form
      setItems([]);
      setCustomerName('');
      setCustomerPhone('');
      setManualDiscount('0');
      setCurrentItem({ productName: '', quantity: '', unitPrice: '' });

      // Navigate back after short delay
      setTimeout(() => {
        navigation.goBack();
      }, 1500);

    } catch (error) {
      console.error('Create bill error:', error);
      Alert.alert('Error', error.response?.data?.message || 'Failed to create bill');
    } finally {
      setLoading(false);
    }
  };


  return (
    <ScrollView style={styles.container}>
      {/* Customer Details */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Customer Details (Optional)</Text>
        <TextInput
          style={styles.input}
          placeholder="Customer Name"
          value={customerName}
          onChangeText={setCustomerName}
        />
        <TextInput
          style={styles.input}
          placeholder="Phone Number"
          value={customerPhone}
          onChangeText={setCustomerPhone}
          keyboardType="phone-pad"
        />
      </View>

      {/* Add Item */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Add Item</Text>

        <Text style={styles.label}>Product Name</Text>
        <TextInput
          style={styles.input}
          placeholder="Type to search..."
          value={currentItem.productName}
          onChangeText={searchProducts}
        />
        {/* Success Message */}
        {success && (
          <View style={styles.successMessage}>
            <Text style={styles.successText}>✅ Bill Created Successfully!</Text>
            <Text style={styles.successSubtext}>Redirecting to home...</Text>
          </View>
        )}
        {suggestions.length > 0 && (
          <View style={styles.suggestions}>
            {suggestions.map((suggestion, index) => (
              <TouchableOpacity
                key={index}
                style={styles.suggestionItem}
                onPress={() => selectSuggestion(suggestion)}
              >
                <Text>{suggestion}</Text>
              </TouchableOpacity>
            ))}
          </View>
        )}

        <View style={styles.row}>
          <View style={styles.halfWidth}>
            <Text style={styles.label}>Quantity</Text>
            <TextInput
              style={styles.input}
              placeholder="Qty"
              value={currentItem.quantity}
              onChangeText={(text) => setCurrentItem({ ...currentItem, quantity: text })}
              keyboardType="numeric"
            />
          </View>
          <View style={styles.halfWidth}>
            <Text style={styles.label}>Unit Price (₹)</Text>
            <TextInput
              style={styles.input}
              placeholder="Price"
              value={currentItem.unitPrice}
              onChangeText={(text) => setCurrentItem({ ...currentItem, unitPrice: text })}
              keyboardType="decimal-pad"
            />
          </View>
        </View>

        <TouchableOpacity style={styles.addButton} onPress={addItem}>
          <Text style={styles.addButtonText}>+ Add Item</Text>
        </TouchableOpacity>
      </View>

      {/* Items List */}
      {items.length > 0 && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Bill Items</Text>
          {items.map((item, index) => (
            <View key={index} style={styles.itemCard}>
              <View style={styles.itemInfo}>
                <Text style={styles.itemName}>{item.productName}</Text>
                <Text style={styles.itemDetails}>
                  {item.quantity} × ₹{item.unitPrice.toFixed(2)} = ₹{item.totalPrice.toFixed(2)}
                </Text>
              </View>
              <TouchableOpacity onPress={() => removeItem(index)}>
                <Text style={styles.removeButton}>✕</Text>
              </TouchableOpacity>
            </View>
          ))}
        </View>
      )}

      {/* Payment & Discount */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Payment Details</Text>

        <Text style={styles.label}>Payment Method</Text>
        <View style={styles.paymentMethods}>
          {['CASH', 'UPI', 'CARD', 'ONLINE'].map((method) => (
            <TouchableOpacity
              key={method}
              style={[
                styles.paymentButton,
                paymentMethod === method && styles.paymentButtonActive,
              ]}
              onPress={() => setPaymentMethod(method)}
            >
              <Text
                style={[
                  styles.paymentButtonText,
                  paymentMethod === method && styles.paymentButtonTextActive,
                ]}
              >
                {method}
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        <Text style={styles.label}>Additional Discount (₹)</Text>
        <TextInput
          style={styles.input}
          placeholder="0"
          value={manualDiscount}
          onChangeText={setManualDiscount}
          keyboardType="decimal-pad"
        />
      </View>

      {/* Bill Summary */}
      {items.length > 0 && (
        <View style={styles.summarySection}>
          <Text style={styles.summaryTitle}>Bill Summary</Text>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Subtotal:</Text>
            <Text style={styles.summaryValue}>₹{totals.subtotal.toFixed(2)}</Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Fixed Discount (10%):</Text>
            <Text style={styles.discountValue}>-₹{totals.fixedDiscount.toFixed(2)}</Text>
          </View>
          {totals.manualDiscountAmount > 0 && (
            <View style={styles.summaryRow}>
              <Text style={styles.summaryLabel}>Additional Discount:</Text>
              <Text style={styles.discountValue}>-₹{totals.manualDiscountAmount.toFixed(2)}</Text>
            </View>
          )}
          <View style={styles.divider} />
          <View style={styles.summaryRow}>
            <Text style={styles.totalLabel}>Total Amount:</Text>
            <Text style={styles.totalValue}>₹{totals.total.toFixed(2)}</Text>
          </View>
        </View>
      )}

      {/* Create Bill Button */}
      <TouchableOpacity
        style={[styles.createButton, (items.length === 0 || loading) && styles.createButtonDisabled]}
        onPress={createBill}
        disabled={items.length === 0 || loading}
      >
        <Text style={styles.createButtonText}>
          {loading ? 'Creating...' : 'Create Bill'}
        </Text>
      </TouchableOpacity>

      <View style={{ height: 30 }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  section: {
    backgroundColor: 'white',
    margin: 10,
    padding: 15,
    borderRadius: 10,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
  },
  label: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
    marginTop: 10,
  },
  input: {
    backgroundColor: '#f5f5f5',
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
    marginBottom: 10,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  halfWidth: {
    width: '48%',
  },
  suggestions: {
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    marginBottom: 10,
    maxHeight: 150,
  },
  suggestionItem: {
    padding: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  addButton: {
    backgroundColor: '#4CAF50',
    padding: 15,
    borderRadius: 8,
    marginTop: 10,
  },
  addButtonText: {
    color: 'white',
    textAlign: 'center',
    fontSize: 16,
    fontWeight: 'bold',
  },
  itemCard: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 12,
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    marginBottom: 10,
  },
  itemInfo: {
    flex: 1,
  },
  itemName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  itemDetails: {
    fontSize: 14,
    color: '#666',
    marginTop: 2,
  },
  removeButton: {
    fontSize: 24,
    color: '#f44336',
    paddingHorizontal: 10,
  },
  paymentMethods: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
    marginBottom: 10,
  },
  paymentButton: {
    flex: 1,
    minWidth: '45%',
    padding: 12,
    borderRadius: 8,
    borderWidth: 2,
    borderColor: '#ddd',
    alignItems: 'center',
  },
  paymentButtonActive: {
    borderColor: '#3b5998',
    backgroundColor: '#3b5998',
  },
  paymentButtonText: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#666',
  },
  paymentButtonTextActive: {
    color: 'white',
  },
  summarySection: {
    backgroundColor: 'white',
    margin: 10,
    padding: 15,
    borderRadius: 10,
  },
  summaryTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
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
  discountValue: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#f44336',
  },
  divider: {
    height: 1,
    backgroundColor: '#ddd',
    marginVertical: 10,
  },
  totalLabel: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  totalValue: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#4CAF50',
  },
  createButton: {
    backgroundColor: '#3b5998',
    margin: 10,
    padding: 18,
    borderRadius: 10,
    alignItems: 'center',
  },
  createButtonDisabled: {
    opacity: 0.5,
  },
  createButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
  successMessage: {
    backgroundColor: '#4CAF50',
    margin: 10,
    padding: 20,
    borderRadius: 10,
    alignItems: 'center',
  },
  successText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  successSubtext: {
    color: 'white',
    fontSize: 14,
  },
});

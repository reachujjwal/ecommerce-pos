import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { Package, MapPin, XCircle } from 'lucide-react';
import { toast } from 'sonner';
import api from '../../utils/api';

const Orders = () => {
    const navigate = useNavigate();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            const jwt = localStorage.getItem('jwt');
            if (!jwt) { navigate('/auth/login'); return; }
            const response = await api.get('/api/orders/customer/me');
            console.log('Orders loaded:', response.data);
            setOrders(response.data);
        } catch (error) {
            console.error('Error fetching orders:', error);
        } finally {
            setLoading(false);
        }
    };

    const cancelOrder = async (orderId) => {
        if (!confirm('Are you sure you want to cancel this order?')) return;
        try {
            await api.put(`/api/orders/${orderId}/cancel`);
            toast.success('Order cancelled successfully');
            fetchOrders();
        } catch (error) {
            console.error('Error cancelling order:', error);
            toast.error(error.response?.data?.message || 'Failed to cancel order');
        }
    };

    const getStatusColor = (status) => {
        const colors = {
            CONFIRMED: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
            PROCESSING: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
            READY_FOR_PICKUP: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
            OUT_FOR_DELIVERY: 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400',
            DELIVERED: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
            CANCELLED: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
        };
        return colors[status] || 'bg-muted text-muted-foreground';
    };

    const canCancel = (status) => {
        if (!status) return false;
        return !['DELIVERED', 'CANCELLED'].includes(status);
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[60vh]">
                <div className="text-xl text-foreground animate-pulse">Loading orders...</div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold text-foreground">My Orders</h1>
                <button
                    onClick={() => navigate('/customer/products')}
                    className="text-primary hover:text-primary/80 font-medium text-sm transition-colors"
                >
                    Continue Shopping →
                </button>
            </div>

            {orders.length === 0 ? (
                <div className="text-center py-16 bg-card border border-border rounded-xl">
                    <p className="text-muted-foreground">No orders found</p>
                </div>
            ) : (
                <div className="space-y-6">
                    {orders.map(order => (
                        <div key={order.id} className="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
                            {/* Order Header */}
                            <div className="bg-muted/30 px-6 py-4 border-b border-border flex flex-wrap items-center justify-between gap-4">
                                <div className="space-y-1">
                                    <p className="text-xs text-muted-foreground uppercase tracking-wider font-semibold">Order ID</p>
                                    <p className="font-mono text-sm text-foreground">#{order.id}</p>
                                </div>
                                <div className="space-y-1">
                                    <p className="text-xs text-muted-foreground uppercase tracking-wider font-semibold">Placed On</p>
                                    <p className="text-sm text-foreground">{order.createdAt ? new Date(order.createdAt).toLocaleDateString() : 'N/A'}</p>
                                </div>
                                <div className="space-y-1">
                                    <p className="text-xs text-muted-foreground uppercase tracking-wider font-semibold">Total Amount</p>
                                    <p className="font-bold text-foreground">₹{order.totalAmount?.toFixed(2)}</p>
                                </div>
                                <div className="flex items-center gap-3">
                                    <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide ${getStatusColor(order.status)}`}>
                                        {order.status}
                                    </span>
                                </div>
                            </div>

                            {/* Order Items */}
                            <div className="px-6 py-4">
                                <div className="divide-y divide-border">
                                    {order.items?.map(item => (
                                        <div key={item.id} className="py-4 flex items-center gap-4">
                                            <div className="w-16 h-16 bg-muted rounded-lg flex-shrink-0 flex items-center justify-center overflow-hidden">
                                                {item.productImage ? (
                                                    <img src={item.productImage} alt={item.productName} className="w-full h-full object-cover" />
                                                ) : (
                                                    <span className="text-[10px] text-muted-foreground">No Image</span>
                                                )}
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="font-semibold text-foreground truncate">{item.productName}</p>
                                                <p className="text-xs text-muted-foreground mt-0.5">Quantity: {item.quantity}</p>
                                            </div>
                                            <div className="text-right">
                                                <p className="font-bold text-foreground">₹{item.price?.toFixed(2)}</p>
                                                <p className="text-xs text-muted-foreground">₹{item.price?.toFixed(2)} each</p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            {/* Order Footer */}
                            <div className="bg-muted/30 px-6 py-4 border-t border-border flex items-center justify-between">
                                <div className="text-sm">
                                    <span className="text-muted-foreground">Delivery Method: </span>
                                    <span className="font-medium text-foreground">{order.deliveryType}</span>
                                </div>
                                {canCancel(order.status) && (
                                    <button
                                        onClick={() => cancelOrder(order.id)}
                                        className="text-destructive hover:bg-destructive/10 px-4 py-2 rounded-lg text-sm font-bold transition-colors"
                                    >
                                        Cancel Order
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Orders;

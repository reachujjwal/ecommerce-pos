import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { Trash2, Plus, Minus, ShoppingBag } from 'lucide-react';
import { toast } from 'sonner';
import api from '../../utils/api';

const Cart = () => {
    const navigate = useNavigate();
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => { fetchCart(); }, []);

    const fetchCart = async () => {
        try {
            const jwt = localStorage.getItem('jwt');
            if (!jwt) { navigate('/auth/login'); return; }
            const response = await api.get('/api/cart');
            setCart(response.data);
        } catch (error) {
            console.error('Error fetching cart:', error);
        } finally {
            setLoading(false);
        }
    };

    const updateQuantity = async (itemId, newQuantity) => {
        if (newQuantity < 1) return;
        try {
            await api.put(`/api/cart/items/${itemId}?quantity=${newQuantity}`);
            fetchCart();
        } catch (error) {
            console.error('Error updating quantity:', error);
            toast.error('Failed to update quantity');
        }
    };

    const removeItem = async (itemId) => {
        try {
            await api.delete(`/api/cart/items/${itemId}`);
            fetchCart();
        } catch (error) {
            console.error('Error removing item:', error);
            toast.error('Failed to remove item');
        }
    };

    const clearCart = async () => {
        if (!confirm('Are you sure you want to clear your cart?')) return;
        try {
            await api.delete('/api/cart');
            fetchCart();
        } catch (error) {
            console.error('Error clearing cart:', error);
            toast.error('Failed to clear cart');
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[60vh]">
                <div className="text-xl text-foreground animate-pulse">Loading cart...</div>
            </div>
        );
    }

    const isEmpty = !cart || !cart.items || cart.items.length === 0;

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold text-foreground">Shopping Cart</h1>
                <button
                    onClick={() => navigate('/customer/products')}
                    className="text-primary hover:text-primary/80 font-medium text-sm transition-colors"
                >
                    ← Continue Shopping
                </button>
            </div>

            {isEmpty ? (
                <div className="text-center py-16 bg-card rounded-xl border border-border">
                    <ShoppingBag className="w-20 h-20 text-muted-foreground/30 mx-auto mb-4" />
                    <h2 className="text-xl font-semibold text-foreground mb-2">Your cart is empty</h2>
                    <p className="text-muted-foreground mb-6">Add some products to get started!</p>
                    <button
                        onClick={() => navigate('/customer/products')}
                        className="bg-primary text-primary-foreground px-6 py-2.5 rounded-lg hover:bg-primary/90 font-medium transition-colors"
                    >
                        Browse Products
                    </button>
                </div>
            ) : (
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Cart Items */}
                    <div className="lg:col-span-2 space-y-4">
                        <div className="flex items-center justify-between">
                            <h2 className="text-base font-semibold text-foreground">
                                {cart.itemCount} {cart.itemCount === 1 ? 'Item' : 'Items'}
                            </h2>
                            <button
                                onClick={clearCart}
                                className="text-destructive hover:text-destructive/80 text-sm font-medium transition-colors"
                            >
                                Clear Cart
                            </button>
                        </div>

                        {cart.items.map(item => (
                            <div key={item.itemId} className="bg-card border border-border rounded-xl shadow-sm p-4">
                                <div className="flex gap-4">
                                    <div className="w-20 h-20 bg-muted rounded-lg flex-shrink-0 overflow-hidden">
                                        {item.productImage ? (
                                            <img src={item.productImage} alt={item.productName} className="w-full h-full object-cover" />
                                        ) : (
                                            <div className="w-full h-full flex items-center justify-center text-muted-foreground text-xs">No Image</div>
                                        )}
                                    </div>

                                    <div className="flex-1 min-w-0">
                                        <h3 className="font-semibold text-foreground mb-0.5 truncate">{item.productName}</h3>
                                        <p className="text-xs text-muted-foreground mb-3">SKU: {item.sku}</p>
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center gap-2">
                                                <button
                                                    onClick={() => updateQuantity(item.itemId, item.quantity - 1)}
                                                    className="p-1.5 rounded-lg border border-border hover:bg-muted text-foreground transition-colors disabled:opacity-40"
                                                    disabled={item.quantity <= 1}
                                                >
                                                    <Minus className="w-3 h-3" />
                                                </button>
                                                <span className="font-semibold text-foreground w-8 text-center">{item.quantity}</span>
                                                <button
                                                    onClick={() => updateQuantity(item.itemId, item.quantity + 1)}
                                                    className="p-1.5 rounded-lg border border-border hover:bg-muted text-foreground transition-colors"
                                                >
                                                    <Plus className="w-3 h-3" />
                                                </button>
                                            </div>
                                            <button
                                                onClick={() => removeItem(item.itemId)}
                                                className="text-muted-foreground hover:text-destructive p-2 rounded-lg hover:bg-destructive/10 transition-colors"
                                            >
                                                <Trash2 className="w-4 h-4" />
                                            </button>
                                        </div>
                                    </div>

                                    <div className="text-right flex-shrink-0">
                                        <p className="font-bold text-foreground">₹{item.total?.toFixed(2)}</p>
                                        <p className="text-xs text-muted-foreground">₹{item.price?.toFixed(2)} each</p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Order Summary */}
                    <div className="lg:col-span-1">
                        <div className="bg-card border border-border rounded-xl shadow-sm p-6 sticky top-4">
                            <h2 className="text-lg font-semibold text-foreground mb-4">Order Summary</h2>

                            <div className="space-y-3 mb-4">
                                <div className="flex justify-between text-sm text-muted-foreground">
                                    <span>Subtotal</span>
                                    <span className="text-foreground font-medium">₹{cart.subtotal?.toFixed(2)}</span>
                                </div>
                                <div className="flex justify-between text-sm text-muted-foreground">
                                    <span>Delivery Fee</span>
                                    <span className="text-green-500 font-medium">Calculated at checkout</span>
                                </div>
                            </div>

                            <div className="border-t border-border pt-3 mb-5">
                                <div className="flex justify-between text-base font-bold text-foreground">
                                    <span>Total</span>
                                    <span>₹{cart.subtotal?.toFixed(2)}</span>
                                </div>
                            </div>

                            <button
                                onClick={() => navigate('/customer/checkout')}
                                className="w-full bg-primary text-primary-foreground py-3 rounded-lg hover:bg-primary/90 font-semibold transition-colors"
                            >
                                Proceed to Checkout
                            </button>

                            <button
                                onClick={() => navigate('/customer/products')}
                                className="w-full mt-3 border border-border text-foreground py-3 rounded-lg hover:bg-muted font-medium transition-colors"
                            >
                                Continue Shopping
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Cart;

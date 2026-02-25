import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { MapPin, Package, CreditCard, Truck } from 'lucide-react';
import { toast } from 'sonner';
import api from '../../utils/api';
import { useBranch } from '../../contexts/BranchContext';

const Checkout = () => {
    const navigate = useNavigate();
    const { selectedBranch: contextSelectedBranch } = useBranch(); // Renamed to avoid conflict with local state
    const [cart, setCart] = useState(null);
    const [addresses, setAddresses] = useState([]);
    const [loading, setLoading] = useState(true);

    const [deliveryType, setDeliveryType] = useState('DELIVERY');
    const [selectedAddress, setSelectedAddress] = useState(null);
    const [paymentType, setPaymentType] = useState('CASH_ON_DELIVERY');
    const [notes, setNotes] = useState('');
    const [processing, setProcessing] = useState(false);

    // Redirect if no branch selected
    useEffect(() => {
        if (!contextSelectedBranch) {
            navigate('/customer/select-branch');
        }
    }, [contextSelectedBranch, navigate]);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const jwt = localStorage.getItem('jwt');
            if (!jwt) {
                navigate('/auth/login');
                return;
            }

            const [cartRes, addressesRes] = await Promise.all([
                api.get('/api/cart'),
                api.get('/api/customer/addresses')
            ]);

            setCart(cartRes.data);
            setAddresses(addressesRes.data);

            // Auto-select default address
            const defaultAddr = addressesRes.data.find(a => a.isDefault);
            if (defaultAddr) setSelectedAddress(defaultAddr.id);

        } catch (error) {
            console.error('Error fetching data:', error);
            toast.error('Failed to load checkout data');
        } finally {
            setLoading(false);
        }
    };

    const handleCheckout = async () => {
        if (deliveryType === 'DELIVERY' && !selectedAddress) {
            toast.error('Please select a delivery address');
            return;
        }

        setProcessing(true);
        try {
            const jwt = localStorage.getItem('jwt');
            const checkoutData = {
                deliveryType,
                addressId: deliveryType === 'DELIVERY' ? selectedAddress : null,
                branchId: contextSelectedBranch.id,
                paymentType,
                notes: notes || null
            };

            const response = await api.post('/api/orders/checkout', checkoutData);

            toast.success('Order placed successfully!');
            navigate('/customer/orders');
        } catch (error) {
            console.error('Error placing order:', error);
            toast.error(error.response?.data?.message || 'Failed to place order');
        } finally {
            setProcessing(false);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="text-xl">Loading...</div>
            </div>
        );
    }

    if (!cart || cart.items.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center min-h-screen bg-background">
                <p className="text-xl text-muted-foreground mb-4">Your cart is empty</p>
                <button
                    onClick={() => navigate('/customer/products')}
                    className="bg-primary text-primary-foreground px-6 py-2 rounded-lg hover:bg-primary/90 transition-colors"
                >
                    Browse Products
                </button>
            </div>
        );
    }

    const deliveryFee = deliveryType === 'DELIVERY' ? 50 : 0;
    const total = cart.subtotal + deliveryFee;

    return (
        <div className="min-h-screen bg-background">
            <header className="bg-card border-b border-border shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <h1 className="text-2xl font-bold text-foreground">Checkout</h1>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Checkout Form */}
                    <div className="lg:col-span-2 space-y-6">
                        {/* Delivery Type */}
                        <div className="bg-card border border-border rounded-lg shadow-sm p-6">
                            <h2 className="text-lg font-semibold text-foreground mb-4 flex items-center gap-2">
                                <Truck className="w-5 h-5 text-primary" />
                                Delivery Method
                            </h2>
                            <div className="grid grid-cols-2 gap-4">
                                <button
                                    onClick={() => setDeliveryType('DELIVERY')}
                                    className={`p-4 border-2 rounded-lg transition-all ${deliveryType === 'DELIVERY'
                                        ? 'border-primary bg-primary/10 text-primary'
                                        : 'border-border bg-background text-muted-foreground hover:border-muted-foreground'
                                        } `}
                                >
                                    <Truck className="w-6 h-6 mx-auto mb-2" />
                                    <p className="font-medium">Home Delivery</p>
                                    <p className="text-sm opacity-80">₹50 delivery fee</p>
                                </button>
                                <button
                                    onClick={() => setDeliveryType('PICKUP')}
                                    className={`p-4 border-2 rounded-lg transition-all ${deliveryType === 'PICKUP'
                                        ? 'border-primary bg-primary/10 text-primary'
                                        : 'border-border bg-background text-muted-foreground hover:border-muted-foreground'
                                        } `}
                                >
                                    <Package className="w-6 h-6 mx-auto mb-2" />
                                    <p className="font-medium">Store Pickup</p>
                                    <p className="text-sm opacity-80">Free</p>
                                </button>
                            </div>
                        </div>

                        {/* Address Selection (Delivery) */}
                        {deliveryType === 'DELIVERY' && (
                            <div className="bg-card border border-border rounded-lg shadow-sm p-6">
                                <div className="flex items-center justify-between mb-4">
                                    <h2 className="text-lg font-semibold text-foreground flex items-center gap-2">
                                        <MapPin className="w-5 h-5 text-primary" />
                                        Delivery Address
                                    </h2>
                                    <button
                                        onClick={() => navigate('/customer/addresses')}
                                        className="text-blue-600 hover:text-blue-700 text-sm font-medium"
                                    >
                                        Manage Addresses
                                    </button>
                                </div>
                                {addresses.length === 0 ? (
                                    <div className="text-center py-4">
                                        <p className="text-muted-foreground mb-3">No addresses found</p>
                                        <button
                                            onClick={() => navigate('/customer/addresses')}
                                            className="text-primary hover:text-primary/80 font-medium transition-colors"
                                        >
                                            Add Address
                                        </button>
                                    </div>
                                ) : (
                                    <div className="space-y-3">
                                        {addresses.map(addr => (
                                            <label
                                                key={addr.id}
                                                className={`block p-4 border-2 rounded-lg cursor-pointer transition-all ${selectedAddress === addr.id
                                                    ? 'border-primary bg-primary/10'
                                                    : 'border-border bg-background hover:border-muted-foreground'
                                                    } `}
                                            >
                                                <input
                                                    type="radio"
                                                    name="address"
                                                    value={addr.id}
                                                    checked={selectedAddress === addr.id}
                                                    onChange={() => setSelectedAddress(addr.id)}
                                                    className="sr-only"
                                                />
                                                <div className="flex items-start justify-between">
                                                    <div>
                                                        <p className="font-semibold text-foreground">{addr.fullName}</p>
                                                        <p className="text-sm text-muted-foreground mt-1">{addr.fullAddress}</p>
                                                        <p className="text-sm text-muted-foreground">📞 {addr.phone}</p>
                                                    </div>
                                                    {addr.isDefault && (
                                                        <span className="text-[10px] bg-primary/20 text-primary px-2 py-0.5 rounded-full font-bold uppercase tracking-wider">
                                                            Default
                                                        </span>
                                                    )}
                                                </div>
                                            </label>
                                        ))}
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Pickup Location Info */}
                        {deliveryType === 'PICKUP' && (
                            <div className="bg-card border border-border rounded-lg shadow-sm p-6">
                                <h2 className="text-lg font-semibold text-foreground mb-4 flex items-center gap-2">
                                    <Package className="w-5 h-5 text-primary" />
                                    Pickup Location
                                </h2>
                                <div className="bg-muted border border-border rounded-lg p-4">
                                    <p className="font-semibold text-foreground">{contextSelectedBranch.name}</p>
                                    <p className="text-sm text-muted-foreground mt-1">{contextSelectedBranch.address}</p>
                                    {contextSelectedBranch.phoneNumber && (
                                        <p className="text-sm text-muted-foreground mt-1">Phone: {contextSelectedBranch.phoneNumber}</p>
                                    )}
                                </div>
                            </div>
                        )}

                        {/* Payment Method */}
                        <div className="bg-card border border-border rounded-lg shadow-sm p-6">
                            <h2 className="text-lg font-semibold text-foreground mb-4 flex items-center gap-2">
                                <CreditCard className="w-5 h-5 text-primary" />
                                Payment Method
                            </h2>
                            <div className="space-y-3">
                                <label className="flex items-center gap-3 p-4 border border-border rounded-lg cursor-pointer hover:bg-muted transition-colors">
                                    <input
                                        type="radio"
                                        className="w-4 h-4 accent-primary"
                                        name="payment"
                                        value="CASH_ON_DELIVERY"
                                        checked={paymentType === 'CASH_ON_DELIVERY'}
                                        onChange={(e) => setPaymentType(e.target.value)}
                                    />
                                    <div>
                                        <p className="font-medium text-foreground">Cash on Delivery</p>
                                        <p className="text-sm text-muted-foreground">Pay when you receive</p>
                                    </div>
                                </label>
                                <label className="flex items-center gap-3 p-4 border border-border rounded-lg cursor-pointer hover:bg-muted transition-colors">
                                    <input
                                        type="radio"
                                        className="w-4 h-4 accent-primary"
                                        name="payment"
                                        value="ONLINE"
                                        checked={paymentType === 'ONLINE'}
                                        onChange={(e) => setPaymentType(e.target.value)}
                                    />
                                    <div>
                                        <p className="font-medium text-foreground">Online Payment</p>
                                        <p className="text-sm text-muted-foreground">UPI, Cards, Net Banking</p>
                                    </div>
                                </label>
                            </div>
                        </div>

                        {/* Order Notes */}
                        <div className="bg-card border border-border rounded-lg shadow-sm p-6">
                            <h2 className="text-lg font-semibold text-foreground mb-4">Order Notes (Optional)</h2>
                            <textarea
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                placeholder="Any special instructions..."
                                rows={3}
                                className="w-full px-4 py-2 bg-background border border-border rounded-lg text-foreground placeholder:text-muted-foreground focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-all"
                            />
                        </div>
                    </div>

                    {/* Order Summary */}
                    <div className="lg:col-span-1">
                        <div className="bg-card border border-border rounded-lg shadow-sm p-6 sticky top-4">
                            <h2 className="text-lg font-semibold text-foreground mb-4">Order Summary</h2>

                            <div className="space-y-2 mb-4">
                                {cart.items.map(item => (
                                    <div key={item.itemId} className="flex justify-between text-sm">
                                        <span className="text-muted-foreground">{item.productName} x{item.quantity}</span>
                                        <span className="text-foreground font-medium">₹{item.total?.toFixed(2)}</span>
                                    </div>
                                ))}
                            </div>

                            <div className="border-t border-border pt-3 space-y-2 mb-4">
                                <div className="flex justify-between text-muted-foreground">
                                    <span>Subtotal</span>
                                    <span className="text-foreground">₹{cart.subtotal?.toFixed(2)}</span>
                                </div>
                                <div className="flex justify-between text-muted-foreground">
                                    <span>Delivery Fee</span>
                                    <span className={deliveryFee === 0 ? 'text-green-500' : 'text-foreground'}>
                                        {deliveryFee === 0 ? 'Free' : `₹${deliveryFee.toFixed(2)} `}
                                    </span>
                                </div>
                            </div>

                            <div className="border-t border-border pt-3 mb-6">
                                <div className="flex justify-between text-lg font-bold text-foreground">
                                    <span>Total</span>
                                    <span>₹{total.toFixed(2)}</span>
                                </div>
                            </div>

                            <button
                                onClick={handleCheckout}
                                disabled={processing}
                                className="w-full bg-primary text-primary-foreground py-3 rounded-lg hover:bg-primary/90 font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed transform active:scale-[0.98]"
                            >
                                {processing ? 'Processing...' : 'Place Order'}
                            </button>
                        </div>
                    </div>
                </div >
            </main >
        </div >
    );
};

export default Checkout;

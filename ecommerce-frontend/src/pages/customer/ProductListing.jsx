import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { ShoppingCart, Search, MapPin } from 'lucide-react';
import { toast } from 'sonner';
import api from '../../utils/api';
import { useBranch } from '../../contexts/BranchContext';

const ProductListing = () => {
    const navigate = useNavigate();
    const { selectedBranch, clearBranch } = useBranch();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('all');
    const [cart, setCart] = useState([]);

    useEffect(() => {
        if (!selectedBranch) {
            navigate('/customer/select-branch');
        }
    }, [selectedBranch, navigate]);

    useEffect(() => {
        if (selectedBranch) {
            fetchProducts();
            fetchCart();
        }
    }, [selectedBranch]);

    const fetchProducts = async () => {
        try {
            const response = await api.get(`/api/products/store/${selectedBranch.store.id}`);
            setProducts(response.data);
        } catch (error) {
            console.error('Error fetching products:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchCart = async () => {
        try {
            const jwt = localStorage.getItem('jwt');
            if (jwt) {
                const response = await api.get('/api/cart');
                setCart(response.data.items || []);
            }
        } catch (error) {
            console.error('Error fetching cart:', error);
        }
    };

    const addToCart = async (productId) => {
        try {
            const jwt = localStorage.getItem('jwt');
            if (!jwt) { navigate('/auth/login'); return; }
            await api.post('/api/cart/items', { productId, quantity: 1 });
            fetchCart();
            toast.success('Product added to cart!');
        } catch (error) {
            console.error('Error adding to cart:', error);
            toast.error('Failed to add product to cart');
        }
    };

    const filteredProducts = products.filter(product => {
        const matchesSearch = product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            product.description?.toLowerCase().includes(searchQuery.toLowerCase());
        const matchesCategory = selectedCategory === 'all' || product.category === selectedCategory;
        return matchesSearch && matchesCategory;
    });

    const categories = ['all', ...new Set(products.map(p => p.category).filter(Boolean))];

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[60vh]">
                <div className="text-xl text-foreground animate-pulse">Loading products...</div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-bold text-foreground">Shop</h1>
                    {selectedBranch && (
                        <button
                            onClick={() => {
                                if (confirm('Change branch? Your cart will be cleared.')) {
                                    clearBranch();
                                    navigate('/customer/select-branch');
                                }
                            }}
                            className="flex items-center gap-1 text-sm text-primary hover:text-primary/80 mt-1 transition-colors"
                        >
                            <MapPin className="w-4 h-4" />
                            {selectedBranch.name}
                        </button>
                    )}
                </div>
                <button
                    onClick={() => navigate('/customer/cart')}
                    className="relative p-2 text-muted-foreground hover:text-foreground hover:bg-muted rounded-full transition-colors"
                >
                    <ShoppingCart className="w-6 h-6" />
                    {cart.length > 0 && (
                        <span className="absolute -top-1 -right-1 bg-primary text-primary-foreground text-xs rounded-full w-5 h-5 flex items-center justify-center font-bold">
                            {cart.length}
                        </span>
                    )}
                </button>
            </div>

            {/* Search and Filter */}
            <div className="flex gap-3">
                <div className="flex-1 relative">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground w-4 h-4" />
                    <input
                        type="text"
                        placeholder="Search products..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 bg-background border border-border rounded-lg text-foreground placeholder:text-muted-foreground focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-all"
                    />
                </div>
                <select
                    value={selectedCategory}
                    onChange={(e) => setSelectedCategory(e.target.value)}
                    className="px-4 py-2 bg-background border border-border rounded-lg text-foreground focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-all"
                >
                    {categories.map(cat => (
                        <option key={cat} value={cat}>
                            {cat === 'all' ? 'All Categories' : cat}
                        </option>
                    ))}
                </select>
            </div>

            {/* Products Grid */}
            {filteredProducts.length === 0 ? (
                <div className="text-center py-16 bg-card rounded-xl border border-border">
                    <p className="text-muted-foreground text-lg">No products found</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
                    {filteredProducts.map(product => (
                        <div
                            key={product.id}
                            className="bg-card border border-border rounded-xl shadow-sm hover:shadow-md transition-all duration-200 overflow-hidden group"
                        >
                            <div className="aspect-square bg-muted relative overflow-hidden">
                                {product.image ? (
                                    <img
                                        src={product.image}
                                        alt={product.name}
                                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                    />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center text-muted-foreground text-sm">
                                        No Image
                                    </div>
                                )}
                            </div>
                            <div className="p-4">
                                <h3 className="font-semibold text-foreground mb-1 line-clamp-2 text-sm">
                                    {product.name}
                                </h3>
                                {product.brand && (
                                    <p className="text-xs text-muted-foreground mb-2">{product.brand}</p>
                                )}
                                <div className="flex items-baseline gap-2 mb-3">
                                    <span className="text-lg font-bold text-foreground">
                                        ₹{product.sellingPrice?.toFixed(2)}
                                    </span>
                                    {product.mrp && product.mrp > product.sellingPrice && (
                                        <span className="text-xs text-muted-foreground line-through">
                                            ₹{product.mrp?.toFixed(2)}
                                        </span>
                                    )}
                                </div>
                                <button
                                    onClick={() => addToCart(product.id)}
                                    className="w-full bg-primary text-primary-foreground py-2 rounded-lg hover:bg-primary/90 transition-colors font-medium text-sm"
                                >
                                    Add to Cart
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ProductListing;

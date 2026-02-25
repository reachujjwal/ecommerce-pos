import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { MapPin, Store, Phone, Clock } from 'lucide-react';
import { toast } from 'sonner';
import api from '../../utils/api';
import { useBranch } from '../../contexts/BranchContext';

const BranchSelection = () => {
    const navigate = useNavigate();
    const { setSelectedBranch } = useBranch();
    const [branches, setBranches] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchBranches();
    }, []);

    const fetchBranches = async () => {
        try {
            const response = await api.get('/api/branches');
            setBranches(response.data);
        } catch (error) {
            console.error('Error fetching branches:', error);
            toast.error('Failed to load branches');
        } finally {
            setLoading(false);
        }
    };

    const handleSelectBranch = (branch) => {
        setSelectedBranch(branch);
        navigate('/customer/products');
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen bg-background">
                <div className="text-xl text-foreground animate-pulse">Loading branches...</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-background">
            {/* Header */}
            <header className="bg-card border-b border-border shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                    <div className="text-center">
                        <h1 className="text-3xl font-bold text-foreground mb-2">Welcome to Our Store</h1>
                        <p className="text-muted-foreground">Select your preferred branch to start shopping</p>
                    </div>
                </div>
            </header>

            {/* Branch Selection */}
            <main className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                {branches.length === 0 ? (
                    <div className="text-center py-12 bg-card border border-border rounded-xl">
                        <Store className="w-16 h-16 text-muted-foreground/30 mx-auto mb-4" />
                        <p className="text-muted-foreground text-lg">No branches available at the moment</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {branches.map(branch => (
                            <div
                                key={branch.id}
                                onClick={() => handleSelectBranch(branch)}
                                className="bg-card border border-border rounded-xl shadow-sm hover:shadow-lg transition-all cursor-pointer overflow-hidden border-2 hover:border-primary"
                            >
                                <div className="p-6">
                                    <div className="flex items-start justify-between mb-4">
                                        <div className="flex items-center gap-3">
                                            <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
                                                <Store className="w-6 h-6 text-primary" />
                                            </div>
                                            <div>
                                                <h3 className="text-xl font-semibold text-foreground">{branch.name}</h3>
                                                {branch.store && (
                                                    <p className="text-sm text-muted-foreground">{branch.store.name}</p>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    <div className="space-y-3">
                                        {/* Address */}
                                        <div className="flex items-start gap-3 text-muted-foreground">
                                            <MapPin className="w-5 h-5 flex-shrink-0 mt-0.5" />
                                            <p className="text-sm">{branch.address}</p>
                                        </div>

                                        {/* Phone */}
                                        {branch.phoneNumber && (
                                            <div className="flex items-center gap-3 text-muted-foreground">
                                                <Phone className="w-5 h-5 flex-shrink-0" />
                                                <p className="text-sm">{branch.phoneNumber}</p>
                                            </div>
                                        )}

                                        {/* Operating Hours */}
                                        {branch.operatingHours && (
                                            <div className="flex items-center gap-3 text-muted-foreground">
                                                <Clock className="w-5 h-5 flex-shrink-0" />
                                                <p className="text-sm">{branch.operatingHours}</p>
                                            </div>
                                        )}
                                    </div>

                                    <button className="w-full mt-6 bg-primary text-primary-foreground py-3 rounded-lg hover:bg-primary/90 font-medium transition-colors">
                                        Shop at this Branch
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {/* Info Box */}
                <div className="mt-8 bg-primary/5 border border-primary/20 rounded-xl p-6">
                    <div className="flex gap-4">
                        <div className="flex-shrink-0">
                            <svg className="w-6 h-6 text-primary" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                            </svg>
                        </div>
                        <div>
                            <h4 className="text-sm font-bold text-foreground mb-1">Why select a branch?</h4>
                            <p className="text-sm text-muted-foreground">
                                You'll see products available at your selected branch and can choose to pick up your order there or get it delivered to your address.
                            </p>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default BranchSelection;

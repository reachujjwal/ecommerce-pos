import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { MapPin, Plus, Edit2, Trash2, Star } from 'lucide-react';
import { toast } from 'sonner';
import api from '../../utils/api';

const inputClass = "w-full px-4 py-2 bg-background border border-border rounded-lg text-foreground placeholder:text-muted-foreground focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-all";
const labelClass = "block text-sm font-medium text-foreground mb-1";

const INDIAN_STATES = [
    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana",
    "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
    "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana",
    "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh",
    "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
];

const Addresses = () => {
    const navigate = useNavigate();
    const [addresses, setAddresses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [fetchingPincode, setFetchingPincode] = useState(false);
    const [cities, setCities] = useState([]);
    const [formData, setFormData] = useState({
        fullName: '', phone: '', addressLine1: '',
        addressLine2: '', city: '', state: '', zipCode: '', landmark: ''
    });

    useEffect(() => { fetchAddresses(); }, []);

    const fetchAddresses = async () => {
        try {
            const jwt = localStorage.getItem('jwt');
            if (!jwt) { navigate('/auth/login'); return; }
            const response = await api.get('/api/customer/addresses');
            setAddresses(response.data);
        } catch (error) {
            console.error('Error fetching addresses:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingId) {
                await api.put(`/api/customer/addresses/${editingId}`, formData);
                toast.success('Address updated successfully');
            } else {
                await api.post('/api/customer/addresses', formData);
                toast.success('Address added successfully');
            }
            resetForm();
            fetchAddresses();
        } catch (error) {
            console.error('Error saving address:', error);
            toast.error(error.response?.data?.message || 'Failed to save address');
        }
    };

    const handleEdit = (address) => {
        setFormData({
            fullName: address.fullName, phone: address.phone,
            addressLine1: address.addressLine1, addressLine2: address.addressLine2 || '',
            city: address.city, state: address.state, zipCode: address.zipCode, landmark: address.landmark || ''
        });
        setEditingId(address.id);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure you want to delete this address?')) return;
        try {
            await api.delete(`/api/customer/addresses/${id}`);
            toast.success('Address deleted successfully');
            fetchAddresses();
        } catch (error) {
            console.error('Error deleting address:', error);
            toast.error('Failed to delete address');
        }
    };

    const setDefault = async (id) => {
        try {
            await api.put(`/api/customer/addresses/${id}/default`);
            toast.success('Default address updated');
            fetchAddresses();
        } catch (error) {
            console.error('Error setting default:', error);
            toast.error('Failed to set default address');
        }
    };

    const resetForm = () => {
        setFormData({ fullName: '', phone: '', addressLine1: '', addressLine2: '', city: '', state: '', zipCode: '', landmark: '' });
        setEditingId(null);
        setShowForm(false);
        setCities([]);
    };

    const handleZipCodeChange = async (e) => {
        const zip = e.target.value.replace(/\D/g, '').slice(0, 6);
        setFormData(prev => ({ ...prev, zipCode: zip }));

        if (zip.length === 6) {
            setFetchingPincode(true);
            try {
                const response = await fetch(`https://api.postalpincode.in/pincode/${zip}`);
                const data = await response.json();

                if (data[0].Status === "Success") {
                    const postOffices = data[0].PostOffice;
                    const state = postOffices[0].State;
                    const uniqueCities = [...new Set(postOffices.map(po => po.District))];

                    setFormData(prev => ({
                        ...prev,
                        state: state,
                        city: uniqueCities.length === 1 ? uniqueCities[0] : prev.city
                    }));
                    setCities(uniqueCities);
                    toast.success('Address details fetched!');
                } else {
                    toast.error('Invalid Pincode');
                }
            } catch (error) {
                console.error('Error fetching pincode:', error);
                toast.error('Failed to fetch pincode details');
            } finally {
                setFetchingPincode(false);
            }
        }
    };

    const setField = (field) => (e) => setFormData(prev => ({ ...prev, [field]: e.target.value }));

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[60vh]">
                <div className="text-xl text-foreground animate-pulse">Loading addresses...</div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold text-foreground">My Addresses</h1>
                {!showForm && (
                    <button
                        onClick={() => setShowForm(true)}
                        className="flex items-center gap-2 bg-primary text-primary-foreground px-4 py-2 rounded-lg hover:bg-primary/90 font-medium text-sm transition-colors"
                    >
                        <Plus className="w-4 h-4" />
                        Add Address
                    </button>
                )}
            </div>

            {/* Address Form */}
            {showForm && (
                <div className="bg-card border border-border rounded-xl shadow-sm p-6">
                    <div className="flex items-center justify-between mb-5">
                        <h2 className="text-lg font-semibold text-foreground">
                            {editingId ? 'Edit Address' : 'Add New Address'}
                        </h2>
                        <button onClick={resetForm} className="text-muted-foreground hover:text-foreground text-sm transition-colors">
                            Cancel
                        </button>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className={labelClass}>Full Name *</label>
                                <input type="text" required value={formData.fullName} onChange={setField('fullName')} className={inputClass} />
                            </div>
                            <div>
                                <label className={labelClass}>Phone Number *</label>
                                <input type="tel" required pattern="[0-9]{10}" value={formData.phone} onChange={setField('phone')} placeholder="10-digit number" className={inputClass} />
                            </div>
                        </div>

                        <div>
                            <label className={labelClass}>Address Line 1 *</label>
                            <input type="text" required value={formData.addressLine1} onChange={setField('addressLine1')} placeholder="House No., Building Name" className={inputClass} />
                        </div>

                        <div>
                            <label className={labelClass}>Address Line 2</label>
                            <input type="text" value={formData.addressLine2} onChange={setField('addressLine2')} placeholder="Road Name, Area, Colony" className={inputClass} />
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div>
                                <label className={labelClass}>ZIP Code *</label>
                                <div className="relative">
                                    <input
                                        type="text"
                                        required
                                        pattern="[0-9]{6}"
                                        value={formData.zipCode}
                                        onChange={handleZipCodeChange}
                                        placeholder="6 digits"
                                        className={`${inputClass} ${fetchingPincode ? 'pr-10' : ''}`}
                                    />
                                    {fetchingPincode && (
                                        <div className="absolute right-3 top-1/2 -translate-y-1/2">
                                            <div className="w-4 h-4 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div>
                                <label className={labelClass}>State *</label>
                                <select
                                    required
                                    value={formData.state}
                                    onChange={setField('state')}
                                    className={inputClass}
                                >
                                    <option value="">Select State</option>
                                    {INDIAN_STATES.map(state => (
                                        <option key={state} value={state}>{state}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label className={labelClass}>City *</label>
                                {cities.length > 0 ? (
                                    <select
                                        required
                                        value={formData.city}
                                        onChange={setField('city')}
                                        className={inputClass}
                                    >
                                        <option value="">Select City</option>
                                        {cities.map(city => (
                                            <option key={city} value={city}>{city}</option>
                                        ))}
                                    </select>
                                ) : (
                                    <input
                                        type="text"
                                        required
                                        value={formData.city}
                                        onChange={setField('city')}
                                        placeholder="City"
                                        className={inputClass}
                                    />
                                )}
                            </div>
                        </div>

                        <div>
                            <label className={labelClass}>Landmark</label>
                            <input type="text" value={formData.landmark} onChange={setField('landmark')} placeholder="Nearby landmark" className={inputClass} />
                        </div>

                        <div className="flex gap-3 pt-2">
                            <button type="submit" className="flex-1 bg-primary text-primary-foreground py-2.5 rounded-lg hover:bg-primary/90 font-medium transition-colors">
                                {editingId ? 'Update Address' : 'Save Address'}
                            </button>
                            <button type="button" onClick={resetForm} className="px-6 border border-border text-foreground py-2.5 rounded-lg hover:bg-muted font-medium transition-colors">
                                Cancel
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Address List */}
            {addresses.length === 0 && !showForm ? (
                <div className="text-center py-16 bg-card rounded-xl border border-border">
                    <MapPin className="w-16 h-16 text-muted-foreground/30 mx-auto mb-4" />
                    <p className="text-muted-foreground">No addresses saved yet</p>
                    <button onClick={() => setShowForm(true)} className="mt-4 text-primary hover:text-primary/80 font-medium text-sm transition-colors">
                        + Add your first address
                    </button>
                </div>
            ) : (
                <div className="space-y-4">
                    {addresses.map(address => (
                        <div key={address.id} className="bg-card border border-border rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
                            <div className="flex items-start justify-between">
                                <div className="flex-1 min-w-0">
                                    <div className="flex items-center gap-2 mb-2">
                                        <h3 className="font-semibold text-foreground">{address.fullName}</h3>
                                        {address.isDefault && (
                                            <span className="flex items-center gap-1 text-xs bg-primary/10 text-primary px-2 py-0.5 rounded-full font-medium">
                                                <Star className="w-3 h-3 fill-current" />
                                                Default
                                            </span>
                                        )}
                                    </div>
                                    <p className="text-sm text-muted-foreground mb-1">{address.fullAddress}</p>
                                    <p className="text-sm text-muted-foreground">📞 {address.phone}</p>
                                </div>

                                <div className="flex items-center gap-1 ml-4">
                                    {!address.isDefault && (
                                        <button
                                            onClick={() => setDefault(address.id)}
                                            className="p-2 text-muted-foreground hover:text-primary hover:bg-primary/10 rounded-lg transition-colors"
                                            title="Set as default"
                                        >
                                            <Star className="w-4 h-4" />
                                        </button>
                                    )}
                                    <button
                                        onClick={() => handleEdit(address)}
                                        className="p-2 text-muted-foreground hover:text-primary hover:bg-primary/10 rounded-lg transition-colors"
                                    >
                                        <Edit2 className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => handleDelete(address.id)}
                                        className="p-2 text-muted-foreground hover:text-destructive hover:bg-destructive/10 rounded-lg transition-colors"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Addresses;

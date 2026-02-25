import React from 'react';
import { Routes, Route, Navigate } from 'react-router';
import BranchSelection from '../pages/customer/BranchSelection';
import ProductListing from '../pages/customer/ProductListing';
import Cart from '../pages/customer/Cart';
import Checkout from '../pages/customer/Checkout';
import Orders from '../pages/customer/Orders';
import Addresses from '../pages/customer/Addresses';
import CustomerDashboard from '../pages/customer/Dashboard/CustomerDashboard';

const CustomerRoutes = () => {
    return (
        <CustomerDashboard>
            <Routes>
                <Route path="/" element={<Navigate to="/customer/products" replace />} />
                <Route path="/select-branch" element={<BranchSelection />} />
                <Route path="/products" element={<ProductListing />} />
                <Route path="/cart" element={<Cart />} />
                <Route path="/checkout" element={<Checkout />} />
                <Route path="/orders" element={<Orders />} />
                <Route path="/addresses" element={<Addresses />} />
            </Routes>
        </CustomerDashboard>
    );
};

export default CustomerRoutes;

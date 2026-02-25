import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { ShoppingCart, BarChart3, Users, Package, Calendar, Settings, ChevronRight, Play, Pause } from 'lucide-react';

const LiveDemoSection = () => {
  const [activeTab, setActiveTab] = useState('pos');
  const [isPlaying, setIsPlaying] = useState(false);

  const tabs = [
    { id: 'pos', label: 'POS Terminal', icon: <ShoppingCart className="w-5 h-5" /> },
    { id: 'analytics', label: 'Analytics', icon: <BarChart3 className="w-5 h-5" /> },
    { id: 'inventory', label: 'Inventory', icon: <Package className="w-5 h-5" /> },
    { id: 'customers', label: 'Customers', icon: <Users className="w-5 h-5" /> },
  ];

  const features = {
    pos: [
      'Intuitive touchscreen interface',
      'Fast barcode scanning',
      'Quick product search',
      'Customizable hotkeys',
      'Multiple payment methods',
      'Receipt customization'
    ],
    analytics: [
      'Real-time sales dashboard',
      'Product performance metrics',
      'Employee performance tracking',
      'Custom report generation',
      'Data export options',
      'Visual data representation'
    ],
    inventory: [
      'Real-time stock tracking',
      'Low stock alerts',
      'Automated reordering',
      'Supplier management',
      'Stock transfer between stores',
      'Batch and expiry tracking'
    ],
    customers: [
      'Customer profiles',
      'Purchase history',
      'Loyalty program integration',
      'Automated marketing',
      'Customer segmentation',
      'Feedback collection'
    ]
  };

  const togglePlayback = () => {
    setIsPlaying(!isPlaying);
  };

  return (
    <section className="py-16 bg-muted/30 overflow-hidden relative">
      {/* Background Pattern */}
      <div className="absolute inset-0 z-0 opacity-5">
        <div className="grid grid-cols-20 h-full">
          {Array.from({ length: 400 }).map((_, i) => (
            <div key={i} className="border-r border-t border-foreground"></div>
          ))}
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
            See It <span className="text-primary">In Action</span>
          </h2>
          <p className="text-xl text-muted-foreground max-w-3xl mx-auto">
            Experience our intuitive interface and powerful features through our interactive demo
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Left Side - Demo Tabs */}
          <div className="lg:col-span-4 bg-card border border-border rounded-xl shadow-lg p-6 sticky top-24">
            <h3 className="text-xl font-bold text-foreground mb-6">Explore Features</h3>

            <div className="space-y-3 mb-8">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`w-full flex items-center justify-between p-3 rounded-lg transition-colors border ${activeTab === tab.id ? 'bg-primary text-primary-foreground border-primary' : 'bg-muted/50 text-muted-foreground hover:bg-muted border-transparent'}`}
                >
                  <div className="flex items-center space-x-3">
                    <div className={`${activeTab === tab.id ? 'text-primary-foreground' : 'text-primary'}`}>
                      {tab.icon}
                    </div>
                    <span className="font-medium">{tab.label}</span>
                  </div>
                  {activeTab === tab.id && <ChevronRight className="w-5 h-5" />}
                </button>
              ))}
            </div>

            <div className="space-y-4">
              <h4 className="font-medium text-foreground">Key Features:</h4>
              <ul className="space-y-2">
                {features[activeTab].map((feature, index) => (
                  <li key={index} className="flex items-start space-x-2">
                    <div className="w-5 h-5 rounded-full bg-primary/10 text-primary flex items-center justify-center flex-shrink-0 mt-0.5">
                      <span className="text-xs font-bold">{index + 1}</span>
                    </div>
                    <span className="text-muted-foreground">{feature}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="mt-8">
              <Button className="w-full group">
                Request Full Demo
                <ChevronRight className="ml-2 w-4 h-4 group-hover:translate-x-1 transition-transform" />
              </Button>
            </div>
          </div>

          {/* Right Side - Demo Display */}
          <div className="lg:col-span-8">
            <div className="bg-card border border-border rounded-xl shadow-lg overflow-hidden">
              {/* Demo Header */}
              <div className="bg-muted border-b border-border p-4 flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <div className="flex space-x-2">
                    <div className="w-3 h-3 rounded-full bg-red-500"></div>
                    <div className="w-3 h-3 rounded-full bg-yellow-500"></div>
                    <div className="w-3 h-3 rounded-full bg-green-500"></div>
                  </div>
                  <div className="text-sm font-medium text-foreground">
                    {activeTab === 'pos' && 'POS Terminal - Checkout'}
                    {activeTab === 'analytics' && 'Analytics Dashboard'}
                    {activeTab === 'inventory' && 'Inventory Management'}
                    {activeTab === 'customers' && 'Customer Management'}
                  </div>
                </div>
                <div className="flex items-center space-x-3">
                  <button
                    onClick={togglePlayback}
                    className="w-8 h-8 rounded-full bg-primary flex items-center justify-center hover:bg-primary/90 transition-colors"
                  >
                    {isPlaying ? <Pause className="w-4 h-4 text-primary-foreground" /> : <Play className="w-4 h-4 text-primary-foreground" />}
                  </button>
                  <div className="text-xs text-muted-foreground">{isPlaying ? 'Demo Playing' : 'Click to Play'}</div>
                </div>
              </div>

              {/* Demo Content */}
              <div className="p-6 h-[500px] overflow-hidden relative">
                {activeTab === 'pos' && (
                  <div className="h-full">
                    <div className="grid grid-cols-12 gap-4 h-full">
                      {/* Left Side - Products */}
                      <div className="col-span-8 flex flex-col">
                        <div className="bg-muted/50 p-3 rounded-lg mb-4">
                          <div className="flex space-x-2">
                            <input type="text" className="flex-1 p-2 bg-background border border-border rounded text-foreground text-sm" placeholder="Search products..." />
                            <button className="bg-primary text-primary-foreground px-4 py-2 rounded text-sm font-medium">Scan</button>
                          </div>
                        </div>

                        <div className="grid grid-cols-4 gap-3 overflow-y-auto flex-1">
                          {Array.from({ length: 12 }).map((_, i) => (
                            <div key={i} className="bg-card border border-border rounded-lg p-3 flex flex-col items-center text-center hover:border-primary hover:shadow-md transition-all cursor-pointer">
                              <div className="w-12 h-12 bg-muted rounded-lg mb-2 flex items-center justify-center">
                                <div className="w-8 h-8 rounded bg-primary/20"></div>
                              </div>
                              <p className="text-xs font-medium text-foreground mb-1">Product {i + 1}</p>
                              <p className="text-xs text-muted-foreground">₹{((i + 1) * 99).toFixed(2)}</p>
                            </div>
                          ))}
                        </div>
                      </div>

                      {/* Right Side - Cart */}
                      <div className="col-span-4 bg-muted/50 rounded-lg p-4 flex flex-col">
                        <div className="mb-3 pb-3 border-b border-border">
                          <h3 className="font-medium text-foreground mb-1 text-sm">Current Sale</h3>
                          <div className="flex justify-between text-xs">
                            <span className="text-muted-foreground">Items: 3</span>
                            <span className="text-muted-foreground">Total: ₹547.00</span>
                          </div>
                        </div>

                        <div className="flex-1 overflow-y-auto mb-4">
                          {[1, 2, 3].map((item) => (
                            <div key={item} className="flex items-center justify-between py-2 border-b border-border">
                              <div>
                                <p className="text-sm font-medium text-foreground">Product {item}</p>
                                <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                                  <span>₹{(item * 99).toFixed(2)}</span>
                                  <span>×</span>
                                  <span>{item}</span>
                                </div>
                              </div>
                              <p className="font-medium text-foreground text-sm">₹{(item * item * 99).toFixed(2)}</p>
                            </div>
                          ))}
                        </div>

                        <div className="space-y-2 mb-4">
                          <div className="flex justify-between text-xs">
                            <span className="text-muted-foreground">Subtotal</span>
                            <span className="font-medium text-foreground">₹547.00</span>
                          </div>
                          <div className="flex justify-between text-xs">
                            <span className="text-muted-foreground">Tax (18%)</span>
                            <span className="font-medium text-foreground">₹98.46</span>
                          </div>
                          <div className="flex justify-between text-base font-bold text-foreground">
                            <span>Total</span>
                            <span>₹645.46</span>
                          </div>
                        </div>

                        <div className="grid grid-cols-2 gap-2">
                          <Button variant="outline" size="sm">Hold</Button>
                          <Button size="sm">Pay Now</Button>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {activeTab === 'analytics' && (
                  <div className="h-full">
                    <div className="grid grid-cols-3 gap-4 mb-6">
                      {['Today\'s Sales', 'Weekly Revenue', 'Monthly Growth'].map((title, i) => (
                        <div key={i} className="bg-muted/50 border border-border rounded-lg p-4">
                          <p className="text-sm text-muted-foreground mb-1">{title}</p>
                          <p className="text-2xl font-bold text-foreground">
                            {i === 0 ? '₹12,450' : i === 1 ? '₹86,320' : '+18.5%'}
                          </p>
                          <div className="flex items-center mt-2">
                            <div className="w-full bg-muted rounded-full h-2">
                              <div
                                className={`h-2 rounded-full ${i === 2 ? 'bg-green-500' : 'bg-primary'}`}
                                style={{ width: `${i === 0 ? 65 : i === 1 ? 80 : 85}%` }}
                              ></div>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>

                    <div className="grid grid-cols-3 gap-4 mb-6">
                      <div className="col-span-2 bg-muted/50 border border-border rounded-lg p-4">
                        <div className="flex justify-between mb-4">
                          <h3 className="font-medium text-foreground">Sales Overview</h3>
                          <div className="flex space-x-2">
                            <button className="text-xs bg-primary text-primary-foreground px-2 py-1 rounded">Weekly</button>
                            <button className="text-xs bg-muted text-muted-foreground px-2 py-1 rounded">Monthly</button>
                          </div>
                        </div>
                        <div className="h-48 flex items-end space-x-2">
                          {[35, 55, 40, 70, 85, 60, 30].map((height, i) => (
                            <div key={i} className="flex-1 flex flex-col items-center">
                              <div
                                className="w-full bg-primary/80 rounded-t"
                                style={{ height: `${height}%` }}
                              ></div>
                              <p className="text-xs mt-1 text-muted-foreground">{['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'][i]}</p>
                            </div>
                          ))}
                        </div>
                      </div>

                      <div className="bg-muted/50 border border-border rounded-lg p-4">
                        <h3 className="font-medium text-foreground mb-4">Top Categories</h3>
                        <div className="space-y-4">
                          {['Electronics', 'Clothing', 'Groceries', 'Home Goods'].map((category, i) => (
                            <div key={i}>
                              <div className="flex justify-between text-sm mb-1">
                                <span className="text-foreground">{category}</span>
                                <span className="text-muted-foreground">{[32, 28, 21, 19][i]}%</span>
                              </div>
                              <div className="w-full bg-muted rounded-full h-2">
                                <div
                                  className="h-2 rounded-full bg-primary"
                                  style={{ width: `${[32, 28, 21, 19][i]}%` }}
                                ></div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>

                    <div className="bg-muted/50 border border-border rounded-lg p-4">
                      <div className="flex justify-between mb-4">
                        <h3 className="font-medium text-foreground">Recent Transactions</h3>
                        <button className="text-xs text-primary font-medium">View All</button>
                      </div>
                      <div className="grid grid-cols-4 text-xs text-muted-foreground mb-2">
                        <span>Order ID</span>
                        <span>Customer</span>
                        <span>Amount</span>
                        <span>Status</span>
                      </div>
                      {[1, 2, 3].map((i) => (
                        <div key={i} className="grid grid-cols-4 py-2 border-t border-border">
                          <span className="text-sm text-foreground">#ORD-{1000 + i}</span>
                          <span className="text-sm text-muted-foreground">Customer {i}</span>
                          <span className="text-sm text-foreground">₹{(i * 450).toFixed(2)}</span>
                          <span className="text-xs px-2 py-0.5 rounded-full bg-green-500/10 text-green-500 inline-block text-center w-20 border border-green-500/20">
                            Completed
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {activeTab === 'inventory' && (
                  <div className="h-full">
                    <div className="flex space-x-4 mb-6">
                      <div className="flex-1">
                        <input type="text" className="w-full p-2 bg-background border border-border rounded text-sm text-foreground" placeholder="Search inventory..." />
                      </div>
                      <div className="flex space-x-2">
                        <Button variant="outline" size="sm">Filter</Button>
                        <Button size="sm">+ Add Product</Button>
                      </div>
                    </div>

                    <div className="bg-muted/50 border border-border rounded-lg p-4 mb-6">
                      <div className="grid grid-cols-5 gap-4">
                        {['Total Products', 'In Stock', 'Low Stock', 'Out of Stock', 'Categories'].map((title, i) => (
                          <div key={i} className="bg-card border border-border rounded-lg p-3 text-center">
                            <p className="text-xs text-muted-foreground mb-1">{title}</p>
                            <p className="text-lg font-bold text-foreground">
                              {i === 0 ? '1,245' : i === 1 ? '1,100' : i === 2 ? '95' : i === 3 ? '50' : '32'}
                            </p>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div className="bg-card border border-border rounded-lg overflow-hidden">
                      <div className="grid grid-cols-6 bg-muted/50 p-3 text-xs font-medium text-muted-foreground uppercase tracking-wider">
                        <div>Product</div>
                        <div>SKU</div>
                        <div>Category</div>
                        <div>Price</div>
                        <div>Stock</div>
                        <div>Actions</div>
                      </div>

                      {Array.from({ length: 5 }).map((_, i) => (
                        <div key={i} className="grid grid-cols-6 p-3 border-t border-border items-center">
                          <div className="flex items-center space-x-2">
                            <div className="w-8 h-8 bg-muted rounded flex-shrink-0 flex items-center justify-center text-xs font-bold text-primary">P{i + 1}</div>
                            <span className="text-sm font-medium text-foreground">Product {i + 1}</span>
                          </div>
                          <div className="text-xs text-muted-foreground">SKU-{1000 + i}</div>
                          <div className="text-xs text-muted-foreground">
                            {['Electronics', 'Clothing', 'Groceries', 'Home Goods', 'Toys'][i]}
                          </div>
                          <div className="text-sm text-foreground">₹{((i + 1) * 499).toFixed(2)}</div>
                          <div>
                            <span className={`text-[10px] px-2 py-0.5 rounded-full font-medium ${i === 3 ? 'bg-red-500/10 text-red-500 border border-red-500/20' : i === 2 ? 'bg-yellow-500/10 text-yellow-500 border border-yellow-500/20' : 'bg-green-500/10 text-green-500 border border-green-500/20'}`}>
                              {i === 3 ? 'Out of stock' : i === 2 ? 'Low stock' : `${(i + 1) * 25} in stock`}
                            </span>
                          </div>
                          <div className="flex space-x-2">
                            <button className="p-1 text-blue-600 hover:text-blue-800">
                              <Settings className="w-4 h-4" />
                            </button>
                            <button className="p-1 text-primary hover:text-primary-dark">
                              <Package className="w-4 h-4" />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {activeTab === 'customers' && (
                  <div className="h-full">
                    <div className="flex space-x-4 mb-6">
                      <div className="flex-1">
                        <input type="text" className="w-full p-2 bg-background border border-border rounded text-sm text-foreground" placeholder="Search customers..." />
                      </div>
                      <div className="flex space-x-2">
                        <Button variant="outline" size="sm">Filter</Button>
                        <Button size="sm">+ Add Customer</Button>
                      </div>
                    </div>

                    <div className="grid grid-cols-4 gap-4 mb-6">
                      {['Total Customers', 'New This Month', 'Repeat Customers', 'Avg. Spend'].map((title, i) => (
                        <div key={i} className="bg-muted/50 border border-border rounded-lg p-4">
                          <p className="text-xs text-muted-foreground mb-1">{title}</p>
                          <p className="text-lg font-bold text-foreground">
                            {i === 0 ? '3,542' : i === 1 ? '128' : i === 2 ? '68%' : '₹1,250'}
                          </p>
                        </div>
                      ))}
                    </div>

                    <div className="bg-card border border-border rounded-lg overflow-hidden">
                      <div className="grid grid-cols-6 bg-muted/50 p-3 text-xs font-medium text-muted-foreground uppercase tracking-wider">
                        <div>Customer</div>
                        <div>Email</div>
                        <div>Phone</div>
                        <div>Total Spent</div>
                        <div>Last Purchase</div>
                        <div>Actions</div>
                      </div>

                      {Array.from({ length: 5 }).map((_, i) => (
                        <div key={i} className="grid grid-cols-6 p-3 border-t border-border items-center">
                          <div className="flex items-center space-x-2">
                            <div className="w-8 h-8 bg-muted rounded-full flex-shrink-0 flex items-center justify-center text-[10px] font-bold text-primary border border-primary/20">
                              {['RK', 'PS', 'AP', 'SK', 'MJ'][i]}
                            </div>
                            <span className="text-sm font-medium text-foreground">
                              {['Rajesh Kumar', 'Priya Sharma', 'Amit Patel', 'Sneha Kapoor', 'Mohit Jain'][i]}
                            </span>
                          </div>
                          <div className="text-xs text-muted-foreground truncate px-1">customer{i + 1}@example.com</div>
                          <div className="text-xs text-muted-foreground">+91 98765 4{i}210</div>
                          <div className="text-sm text-foreground">₹{((i + 1) * 2500).toLocaleString()}</div>
                          <div className="text-xs text-muted-foreground">{['Today', '2 days ago', '1 week ago', '2 weeks ago', '1 month ago'][i]}</div>
                          <div className="flex space-x-2">
                            <button className="p-1 text-primary hover:text-primary/80">
                              <Users className="w-4 h-4" />
                            </button>
                            <button className="p-1 text-primary hover:text-primary/80">
                              <Calendar className="w-4 h-4" />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Demo Overlay - Shown when not playing */}
                {!isPlaying && (
                  <div className="absolute inset-0 bg-black/50 flex items-center justify-center z-10">
                    <div className="text-center">
                      <button
                        onClick={togglePlayback}
                        className="w-16 h-16 rounded-full bg-primary flex items-center justify-center hover:bg-primary-dark transition-colors mb-4 mx-auto"
                      >
                        <Play className="w-6 h-6 text-white" />
                      </button>
                      <p className="text-white text-lg font-medium">Click to Play Interactive Demo</p>
                    </div>
                  </div>
                )}
              </div>
            </div>

            <div className="mt-6 text-center">
              <p className="text-muted-foreground mb-4">Want to see more? Schedule a personalized demo with our team.</p>
              <Button variant="outline" size="lg">
                Book a Live Demo
              </Button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default LiveDemoSection;
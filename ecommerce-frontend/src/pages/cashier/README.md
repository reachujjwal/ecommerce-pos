# Cashier RetailHub

A comprehensive Point of Sale (POS) system designed for cashiers to efficiently manage sales, customers, and transactions in retail environments.

## 🏗️ Folder Structure

```
src/pages/cashier/
├── components/
│   ├── POSHeader.jsx              # RetailHub header with branch info
│   ├── ReceiptDialog.jsx          # Receipt printing and display
│   └── HeldOrdersDialog.jsx       # Manage held/suspended orders
├── cart/
│   ├── CartSection.jsx            # Main cart display and management
│   ├── CartItem.jsx               # Individual cart item component
│   └── CartSummary.jsx            # Cart totals and summary
├── customer/
│   ├── CustomerLookupPage.jsx     # Customer search and selection
│   ├── CustomerDialog.jsx         # Customer selection modal
│   ├── CustomerForm.jsx           # Add/edit customer form
│   ├── components/                # Customer-related components
│   ├── data/                      # Customer data utilities
│   └── utils/                     # Customer utility functions
├── order/
│   ├── OrderHistoryPage.jsx       # Order history and management
│   ├── OrderTable.jsx             # Orders listing table
│   ├── data.js                    # Order data utilities
│   ├── OrderDetails/              # Order detail components
│   └── pdf/                       # PDF generation utilities
├── payment/
│   ├── PaymentDialog.jsx          # Payment processing modal
│   ├── CustomerPaymentSection.jsx # Payment section in main view
│   └── data.js                    # Payment method data
├── product/
│   ├── ProductSection.jsx         # Product search and display
│   └── ProductCard.jsx            # Individual product card
├── return/
│   ├── ReturnOrderPage.jsx        # Order return processing
│   └── components/                # Return-related components
├── ShiftSummary/
│   ├── ShiftSummaryPage.jsx       # End-of-shift summary
│   ├── components/                # Shift summary components
│   └── data/                      # Shift data utilities
├── CreateOrderPage.jsx            # Main POS interface
├── CashierSideBar.jsx             # Navigation sidebar
├── CashierDashboardLayout.jsx     # Main layout wrapper
├── BranchInfo.jsx                 # Branch information display
└── README.md                      # This file
```

## 🧭 Navigation Menu

- **🛒 Create Order** - Main POS interface for sales
- **👥 Customers** - Customer management and lookup
- **📋 Orders** - Order history and management
- **↩️ Returns** - Process order returns and refunds
- **📊 Shift Summary** - End-of-shift reports and logout

## 🖥️ Main Features

### 1. Create Order (`/cashier`)
**Main POS Interface with 3-Column Layout:**

#### Left Column - Product Section
- **Product Search**: Quick search with keyboard shortcuts (F1)
- **Product Grid**: Visual product cards with images
- **Category Filtering**: Filter products by category
- **Stock Display**: Real-time stock levels
- **Quick Add**: Click to add items to cart

#### Middle Column - Cart Section
- **Cart Items**: List of selected products
- **Quantity Controls**: Increase/decrease quantities
- **Item Removal**: Remove items from cart
- **Cart Summary**: Subtotal, tax, discount, total
- **Held Orders**: Access suspended orders (F4)
- **Clear Cart**: Reset current transaction

#### Right Column - Customer & Payment
- **Customer Selection**: Choose customer for order
- **Customer Info**: Display selected customer details
- **Payment Methods**: Cash, card, digital payments
- **Discount Application**: Apply discounts to order
- **Complete Sale**: Process payment (Ctrl+Enter)

### 2. Customer Management (`/cashier/customers`)
- **Customer Search**: Find existing customers
- **Add Customer**: Create new customer profiles
- **Customer Details**: View customer information
- **Purchase History**: Customer's order history
- **Loyalty Points**: Track and manage points

### 3. Order History (`/cashier/orders`)
- **Order List**: Complete order history
- **Order Details**: Detailed view of each order
- **Order Status**: Track order status
- **Receipt Reprint**: Print duplicate receipts
- **Order Search**: Search by order number, customer, date

### 4. Returns (`/cashier/returns`)
- **Order Lookup**: Find orders for return
- **Item Selection**: Select items to return
- **Return Reason**: Specify return reasons
- **Refund Processing**: Process refunds
- **Return Receipt**: Generate return receipts

### 5. Shift Summary (`/cashier/shift-summary`)
- **Sales Summary**: Total sales for shift
- **Payment Breakdown**: Payment method totals
- **Top Products**: Best-selling items
- **Recent Orders**: Last transactions
- **End Shift**: Complete shift and logout

## 🧩 Key Components

### POSHeader
- Branch information display
- Current user and shift info
- System status indicators
- Quick action buttons

### CartSection
- Real-time cart updates
- Quantity adjustment controls
- Price calculations
- Discount application
- Held orders management

### ProductSection
- Product search functionality
- Category-based filtering
- Stock level indicators
- Quick add to cart
- Product image display

### PaymentDialog
- Multiple payment methods
- Payment validation
- Receipt generation
- Transaction completion
- Error handling

### CustomerDialog
- Customer search and selection
- Customer information display
- Add new customer option
- Customer history access

## ⌨️ Keyboard Shortcuts

- **F1** - Focus on product search
- **F2** - Apply discount
- **F3** - Open customer dialog
- **F4** - Show held orders
- **Ctrl+Enter** - Complete sale
- **Esc** - Close dialogs
- **Tab** - Navigate between sections

## 💳 Payment Methods

- **Cash**: Physical cash payments
- **Card**: Credit/debit card processing
- **Digital**: UPI, QR codes, digital wallets
- **Split Payment**: Multiple payment methods
- **Gift Cards**: Gift card redemption

## 📊 Features

### Customer Management
- **Customer Profiles**: Complete customer information
- **Loyalty Program**: Points tracking and redemption
- **Purchase History**: Customer order history
- **Contact Information**: Phone, email, address
- **Customer Notes**: Special instructions and preferences

### Order Management
- **Order Creation**: Quick and efficient order entry
- **Order Modifications**: Edit orders before payment
- **Order Suspension**: Hold orders for later completion
- **Order History**: Complete transaction history
- **Receipt Generation**: Professional receipts

### Inventory Integration
- **Real-time Stock**: Live stock level updates
- **Low Stock Alerts**: Notifications for low inventory
- **Product Search**: Quick product lookup
- **Category Organization**: Logical product grouping
- **Price Management**: Dynamic pricing support

### Reporting
- **Shift Reports**: End-of-shift summaries
- **Sales Analytics**: Performance metrics
- **Payment Reports**: Payment method analysis
- **Product Reports**: Best-selling items
- **Customer Reports**: Customer activity

## 🔧 Technical Features

### State Management
- **Redux Toolkit**: Centralized state management
- **Cart State**: Persistent cart across sessions
- **Customer State**: Selected customer management
- **Order State**: Order processing and history

### Data Persistence
- **Local Storage**: Cart and preferences
- **API Integration**: Backend data synchronization
- **Offline Support**: Basic offline functionality
- **Data Validation**: Input validation and error handling

### UI/UX
- **Responsive Design**: Works on various screen sizes
- **Touch Support**: Touch-friendly interface
- **Keyboard Navigation**: Full keyboard support
- **Accessibility**: WCAG compliance features
- **Theme Support**: Light/dark mode toggle

## 🚀 Getting Started

1. **Login**: Cashier login with credentials
2. **Branch Selection**: Select working branch
3. **Start Shift**: Begin cashier shift
4. **Create Orders**: Start processing sales
5. **Customer Service**: Assist customers with purchases
6. **End Shift**: Complete shift summary and logout

## 🔐 Security Features

- **User Authentication**: Secure login system
- **Role-based Access**: Cashier-specific permissions
- **Session Management**: Secure session handling
- **Transaction Logging**: Complete audit trail
- **Data Encryption**: Secure data transmission

## 📱 Responsive Design

- **Desktop**: Full-featured POS interface
- **Tablet**: Optimized for tablet screens
- **Mobile**: Basic functionality on mobile
- **Touch Interface**: Touch-friendly controls
- **Print Support**: Receipt and report printing

## 🎯 Best Practices

### For Cashiers
- **Customer Service**: Always greet customers professionally
- **Accuracy**: Double-check prices and quantities
- **Speed**: Use keyboard shortcuts for efficiency
- **Security**: Never leave terminal unattended
- **Cleanup**: Clear cart between transactions

### For Managers
- **Training**: Ensure proper cashier training
- **Monitoring**: Regular shift summary reviews
- **Maintenance**: Keep system updated
- **Backup**: Regular data backup procedures
- **Support**: Provide technical support when needed

## 🔄 Integration Points

- **Inventory System**: Real-time stock updates
- **Customer Database**: Customer information sync
- **Payment Processors**: Payment gateway integration
- **Accounting System**: Financial data export
- **Reporting Tools**: Analytics and reporting
- **Printing System**: Receipt and report printing

## 🎨 Customization

- **Branding**: Custom logos and colors
- **Receipt Templates**: Customizable receipt layouts
- **Product Categories**: Flexible category management
- **Payment Methods**: Configurable payment options
- **Tax Rates**: Dynamic tax calculation
- **Discount Rules**: Flexible discount policies

## 🚨 Troubleshooting

### Common Issues
- **Product Not Found**: Check product search and categories
- **Payment Errors**: Verify payment method configuration
- **Print Issues**: Check printer connection and settings
- **Slow Performance**: Clear cache and restart application
- **Data Sync Issues**: Check network connection

### Support
- **User Manual**: Comprehensive documentation
- **Training Videos**: Step-by-step tutorials
- **Help Desk**: Technical support contact
- **FAQ**: Frequently asked questions
- **Updates**: Regular system updates and patches 
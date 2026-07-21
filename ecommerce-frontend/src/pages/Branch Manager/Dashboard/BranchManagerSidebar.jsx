import React from "react";
import { Link, useLocation, useNavigate } from "react-router";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "@/Redux Toolkit/features/user/userThunks";
import {
  LayoutDashboard,
  ShoppingBag,
  CreditCard,
  Package,
  Users,
  UserCircle,
  FileText,
  Settings,
  LogOut,
  Store
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { RefreshCw } from "lucide-react";

const navLinks = [
  {
    name: "Dashboard",
    path: "/branch/dashboard",
    icon: <LayoutDashboard className="w-5 h-5" />,
  },
  {
    name: "Orders",
    path: "/branch/orders",
    icon: <ShoppingBag className="w-5 h-5" />,
  },
  {
    name: "Refunds",
    path: "/branch/refunds",
    icon: <RefreshCw className="w-5 h-5" />,
  },
  {
    name: "Transactions",
    path: "/branch/transactions",
    icon: <CreditCard className="w-5 h-5" />,
  },
  {
    name: "Inventory",
    path: "/branch/inventory",
    icon: <Package className="w-5 h-5" />,
  },
  {
    name: "Employees",
    path: "/branch/employees",
    icon: <Users className="w-5 h-5" />,
  },
  {
    name: "Customers",
    path: "/branch/customers",
    icon: <UserCircle className="w-5 h-5" />,
  },
  {
    name: "Reports",
    path: "/branch/reports",
    icon: <FileText className="w-5 h-5" />,
  },
  {
    name: "Settings",
    path: "/branch/settings",
    icon: <Settings className="w-5 h-5" />,
  },
];

export default function BranchManagerSidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { branch } = useSelector((state) => state.branch);
  const { userProfile } = useSelector((state) => state.user);
  const { store } = useSelector((state) => state.store);

  const formatRole = (role) => {
    if (!role) return "Manager";
    return role.replace("ROLE_", "").split("_").map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(" ");
  };

  const handleLogout = () => {
    dispatch(logout());
    navigate("/auth/login");
  };

  return (
    <aside className="h-screen w-64 bg-sidebar border-r border-sidebar-border flex flex-col py-6 px-4 shadow-lg">
      <div className="mb-8 flex items-center gap-3">
        <Store className="w-8 h-8 text-primary flex-shrink-0" />
        <div className="flex flex-col overflow-hidden">
          <span className="text-xl font-extrabold text-primary tracking-tight truncate">
            {store?.brand || store?.storeName || branch?.name || 'RetailHub'}
          </span>
          <span className="text-xs text-muted-foreground truncate font-medium">
            {formatRole(userProfile?.role)}
          </span>
        </div>
      </div>
      {branch && (
        <div className="mb-6 px-4 py-3 bg-sidebar-accent rounded-lg">
          <h3 className="font-medium text-sidebar-accent-foreground">{branch.name}</h3>
          <p className="text-xs text-sidebar-accent-foreground/70 mt-1">{branch.address}</p>
        </div>
      )}
      <nav className="flex-1 overflow-y-auto">
        <ul className="space-y-2">
          {navLinks.map((link) => (
            <li key={link.name}>
              <Link
                to={link.path}
                className={`flex items-center gap-3 px-4 py-2 rounded-lg transition-colors text-base font-medium group ${
                  location.pathname.startsWith(link.path)
                    ? "bg-primary text-primary-foreground shadow"
                    : "text-sidebar-foreground hover:bg-primary/10 hover:text-primary"
                }`}
              >
                <span
                  className={`transition-colors ${
                    location.pathname.startsWith(link.path)
                      ? "text-primary-foreground"
                      : "text-sidebar-foreground/60 group-hover:text-primary"
                  }`}
                >
                  {link.icon}
                </span>
                {link.name}
              </Link>
            </li>
          ))}
        </ul>
      </nav>
      <div className="mt-auto">
        <Button
          onClick={handleLogout}
          variant=""
          className="flex items-center gap-3 rounded-lg transition-colors text-base font-medium w-full text-left "
        >
          <LogOut className="w-5 h-5" />
          Logout
        </Button>
      </div>
    </aside>
  );
}
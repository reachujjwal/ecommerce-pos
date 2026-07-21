import React from "react";
import { Link, useLocation, useNavigate } from "react-router";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../../../Redux Toolkit/features/user/userThunks";
import {
  LayoutDashboard,
  Store,
  Users,
  ShoppingCart,
  BarChart2,
  Settings,
  FileText,
  Tag,
  Truck,
  CreditCard,
} from "lucide-react";
import { Button } from "../../../components/ui/button";
import { BadgeDollarSign } from "lucide-react";

const navLinks = [
  {
    name: "Dashboard",
    path: "/store/dashboard",
    icon: <LayoutDashboard className="w-5 h-5" />,
  },
  {
    name: "Stores",
    path: "/store/stores",
    icon: <Store className="w-5 h-5" />,
  },
  {
    name: "Branches",
    path: "/store/branches",
    icon: <Store className="w-5 h-5" />,
  },
  {
    name: "Products",
    path: "/store/products",
    icon: <ShoppingCart className="w-5 h-5" />,
  },
  {
    name: "Categories",
    path: "/store/categories",
    icon: <Tag className="w-5 h-5" />,
  },
  {
    name: "Employees",
    path: "/store/employees",
    icon: <Users className="w-5 h-5" />,
  },
  {
    name: "Alerts",
    path: "/store/alerts",
    icon: <Truck className="w-5 h-5" />,
  },
  {
    name: "Sales",
    path: "/store/sales",
    icon: <BarChart2 className="w-5 h-5" />,
  },

  {
    name: "Reports",
    path: "/store/reports",
    icon: <FileText className="w-5 h-5" />,
  },
  {
    name: "Upgrade Plan",
    path: "/store/upgrade",
    icon: <BadgeDollarSign className="w-5 h-5" />,
  },
  {
    name: "Settings",
    path: "/store/settings",
    icon: <Settings className="w-5 h-5" />,
  },
];

export default function StoreSidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { userProfile } = useSelector((state) => state.user);
  const { store } = useSelector((state) => state.store);

  const formatRole = (role) => {
    if (!role) return "Admin";
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
            {store?.brand || store?.storeName || 'RetailHub'}
          </span>
          <span className="text-xs text-muted-foreground truncate font-medium">
            {formatRole(userProfile?.role)}
          </span>
        </div>
      </div>
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
          className="w-full"
        >
          Logout
        </Button>
      </div>
    </aside>
  );
}

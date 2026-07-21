import React from "react";
import { Link, useLocation, useNavigate } from "react-router";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../../../Redux Toolkit/features/user/userThunks";
import {
    LayoutDashboard,
    ShoppingCart,
    User,
    MapPin,
    LogOut,
    ShoppingBag,
    Store,
} from "lucide-react";
import { Button } from "../../../components/ui/button";

const navLinks = [
    {
        name: "Products",
        path: "/customer/products",
        icon: <ShoppingBag className="w-5 h-5" />,
    },
    {
        name: "Cart",
        path: "/customer/cart",
        icon: <ShoppingCart className="w-5 h-5" />,
    },
    {
        name: "Orders",
        path: "/customer/orders",
        icon: <LayoutDashboard className="w-5 h-5" />,
    },
    {
        name: "Addresses",
        path: "/customer/addresses",
        icon: <MapPin className="w-5 h-5" />,
    },
];

export default function CustomerSidebar() {
    const location = useLocation();
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { userProfile } = useSelector((state) => state.user);

    const formatRole = (role) => {
        if (!role) return "Customer";
        return role.replace("ROLE_", "").split("_").map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(" ");
    };

    const handleLogout = () => {
        dispatch(logout());
        navigate("/auth/login");
    };

    return (
        <aside className="h-screen w-64 bg-sidebar border-r border-sidebar-border flex flex-col py-6 px-4 shadow-lg sticky top-0">
            <div className="mb-8 flex items-center gap-3 px-2">
                <Store className="w-8 h-8 text-primary flex-shrink-0" />
                <div className="flex flex-col overflow-hidden">
                    <span className="text-xl font-extrabold text-primary tracking-tight truncate">
                        RetailHub
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
                                className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 text-base font-medium group ${location.pathname.startsWith(link.path)
                                        ? "bg-primary text-primary-foreground shadow-md translate-x-1"
                                        : "text-sidebar-foreground hover:bg-primary/10 hover:text-primary hover:translate-x-1"
                                    }`}
                            >
                                <span
                                    className={`transition-colors ${location.pathname.startsWith(link.path)
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
            <div className="mt-auto pt-6 border-t border-sidebar-border">
                <Button
                    onClick={handleLogout}
                    variant=""
                    className="w-full flex items-center gap-2 justify-center"
                >
                    <LogOut className="w-4 h-4" />
                    Logout
                </Button>
            </div>
        </aside>
    );
}

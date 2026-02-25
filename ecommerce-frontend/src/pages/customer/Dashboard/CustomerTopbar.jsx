
import { Bell, UserCircle, Search } from "lucide-react";
import { ThemeToggle } from "../../../components/theme-toggle";
import { Input } from "../../../components/ui/input";
import { useSelector } from "react-redux";

export default function CustomerTopbar() {
    const { userProfile } = useSelector((state) => state.user);

    return (
        <header className="w-full h-16 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 border-b flex items-center px-6 justify-between shadow-sm z-10">
            {/* Search */}
            <div className="flex-1 max-w-md relative hidden md:block">
                <Input placeholder="Search products..." className="w-full bg-muted/50 border-muted-foreground/20 focus:bg-background transition-colors" />
                <Search className="absolute right-3 top-2.5 w-4 h-4 text-muted-foreground" />
            </div>

            {/* Right side: Notifications & Profile */}
            <div className="flex items-center gap-4 md:gap-6 ml-auto">
                {/* Theme Toggle */}
                <ThemeToggle />

                {/* Notifications */}
                <button className="relative p-2 hover:bg-muted rounded-full transition-colors">
                    <Bell className="text-muted-foreground w-5 h-5" />
                    <span className="absolute top-1 right-1 bg-primary text-primary-foreground text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center">2</span>
                </button>

                <div className="h-6 w-px bg-border hidden md:block"></div>

                {/* Profile Dropdown */}
                <div className="flex items-center gap-3 cursor-pointer hover:bg-muted p-2 rounded-full md:rounded-lg transition-colors">
                    <div className="w-8 h-8 bg-primary/10 rounded-full flex items-center justify-center font-bold text-primary ring-2 ring-background shadow-sm">
                        <UserCircle className="w-5 h-5" />
                    </div>
                    <div className="hidden md:flex flex-col items-start text-sm">
                        <span className="font-semibold text-foreground leading-none">{userProfile?.fullName || 'Customer'}</span>
                        <span className="text-xs text-muted-foreground leading-none mt-1">{userProfile?.email || 'customer@example.com'}</span>
                    </div>
                </div>
            </div>
        </header>
    );
}

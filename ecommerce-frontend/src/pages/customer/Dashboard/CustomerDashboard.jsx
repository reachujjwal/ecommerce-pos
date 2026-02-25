
import { Outlet } from "react-router";
import CustomerSidebar from "./CustomerSidebar";
import CustomerTopbar from "./CustomerTopbar";

export default function CustomerDashboard({ children }) {
    return (
        <div className="flex h-screen bg-gradient-to-br from-primary/5 via-background to-primary/10 overflow-hidden">
            {/* Sidebar - fixed on desktop, hidden/drawer on mobile (todo) */}
            <div className="hidden md:block">
                <CustomerSidebar />
            </div>

            <div className="flex-1 flex flex-col min-w-0">
                <CustomerTopbar />

                <main className="flex-1 overflow-y-auto p-4 md:p-6 lg:p-8 scroll-smooth">
                    <div className="max-w-7xl mx-auto w-full space-y-6 animate-in fade-in duration-500">
                        {children || <Outlet />}
                    </div>
                </main>
            </div>
        </div>
    );
}

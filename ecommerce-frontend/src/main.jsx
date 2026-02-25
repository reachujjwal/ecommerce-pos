import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { BrowserRouter } from "react-router";
import { Provider } from "react-redux";
import globleState from "./Redux Toolkit/globleState.js";
import { Toaster as ShadcnToaster } from "@/components/ui/use-toast";
import { Toaster } from "sonner";
import { ThemeProvider } from "@/components/theme-provider";
import { BranchProvider } from "./contexts/BranchContext";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
      <Provider store={globleState}>
        <BranchProvider>
          <ThemeProvider
            attribute="class"
            defaultTheme="system"
            enableSystem
            disableTransitionOnChange
          >
            <App />
            <Toaster richColors position="top-right" />
            <ShadcnToaster />
          </ThemeProvider>
        </BranchProvider>
      </Provider>
    </BrowserRouter>
  </StrictMode>
);

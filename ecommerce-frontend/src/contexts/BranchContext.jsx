import React, { createContext, useContext, useState, useEffect } from 'react';

const BranchContext = createContext();

export const useBranch = () => {
    const context = useContext(BranchContext);
    if (!context) {
        throw new Error('useBranch must be used within BranchProvider');
    }
    return context;
};

export const BranchProvider = ({ children }) => {
    const [selectedBranch, setSelectedBranch] = useState(null);

    // Load from localStorage on mount
    useEffect(() => {
        const saved = localStorage.getItem('selectedBranch');
        if (saved) {
            try {
                setSelectedBranch(JSON.parse(saved));
            } catch (error) {
                console.error('Error loading selected branch:', error);
            }
        }
    }, []);

    // Save to localStorage when changed
    useEffect(() => {
        if (selectedBranch) {
            localStorage.setItem('selectedBranch', JSON.stringify(selectedBranch));
        } else {
            localStorage.removeItem('selectedBranch');
        }
    }, [selectedBranch]);

    const clearBranch = () => {
        setSelectedBranch(null);
        localStorage.removeItem('selectedBranch');
    };

    return (
        <BranchContext.Provider value={{ selectedBranch, setSelectedBranch, clearBranch }}>
            {children}
        </BranchContext.Provider>
    );
};

import { ShoppingCart, Menu, X, ChevronDown } from 'lucide-react'
import React, { useState, useEffect } from 'react'
import { Button } from '../../../components/ui/button'
import { useNavigate } from 'react-router'
import { ThemeToggle } from '../../../components/theme-toggle'

const Header = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [isScrolled, setIsScrolled] = useState(false)
  const [activeDropdown, setActiveDropdown] = useState(null)
  const navigate = useNavigate();

  // Handle scroll effect for header
  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 10)
    }

    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  const handleLoginButtonClick = () => {
    navigate('/auth/login');
  }

  const toggleDropdown = (name) => {
    setActiveDropdown(activeDropdown === name ? null : name)
  }
  return (
    <header className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${isScrolled ? 'bg-background/95 shadow-md backdrop-blur-sm border-b' : 'bg-transparent'}`}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20">
          {/* Logo */}
          <div className="flex items-center">
            <div className="flex items-center space-x-2 group cursor-pointer" onClick={() => navigate('/')}>
              <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center shadow-md group-hover:shadow-lg transition-all duration-300 transform group-hover:scale-105">
                <ShoppingCart className="w-6 h-6 text-primary-foreground" />
              </div>
              <span className="text-xl font-bold text-foreground group-hover:text-primary transition-colors">RetailHub</span>
            </div>
          </div>

          {/* Desktop Navigation Removed */}

          {/* CTA Button */}
          <div className="hidden md:flex items-center space-x-4">
            <ThemeToggle />
            <Button onClick={handleLoginButtonClick} variant="outline" className="font-medium">Sign In</Button>
            <Button className="font-medium shadow-md hover:shadow-lg transition-shadow">Request Demo</Button>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden flex items-center space-x-2">
            <ThemeToggle />
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="text-muted-foreground hover:text-primary p-2 rounded-md focus:outline-none focus:ring-2 focus:ring-primary/20"
            >
              {isMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="md:hidden py-4 border-t border-border bg-background">
            <nav className="flex flex-col">
              {/* Mobile Navigation Links Removed */}

              <div className="flex flex-col space-y-3 p-4">
                <Button onClick={handleLoginButtonClick} variant="outline" className="w-full">Sign In</Button>
                <Button className="w-full">Request Demo</Button>
              </div>
            </nav>
          </div>
        )}
      </div>
    </header>
  )
}

export default Header
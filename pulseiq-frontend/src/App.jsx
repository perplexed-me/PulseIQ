import React, { useState } from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './components/auth/Login';
import Registration from './components/auth/Registration';
import Dashboard from './components/Dashboard';

// Main App Component (inside AuthProvider)
const AppContent = () => {
  const [currentView, setCurrentView] = useState('login');
  const { user } = useAuth();

  if (user) {
    return <Dashboard />;
  }

  return (
    <>
      {currentView === 'login' && (
        <Login onSwitchToRegister={() => setCurrentView('register')} />
      )}
      {currentView === 'register' && (
        <Registration onSwitchToLogin={() => setCurrentView('login')} />
      )}
    </>
  );
};

// Root App with Provider
const App = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

export default App;
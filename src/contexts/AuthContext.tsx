import React, { createContext, useContext, useState, useEffect } from 'react';

export interface User {
  id: string;
  email?: string;
  phone?: string;
  role: 'doctor' | 'patient' | 'technician' | 'admin';
  name: string;
  status?: 'pending' | 'approved' | 'rejected';
}

interface AuthContextType {
  user: User | null;
  login: (credentials: { identifier: string; password: string }) => Promise<boolean>;
  logout: () => void;
  isLoading: boolean;
  setUser: (user: User | null) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<User | null>(null);

  const updateUser = (newUser: User | null) => {
    setUser(newUser);
    if (newUser) {
      localStorage.setItem('pulseiq_user', JSON.stringify(newUser));
    } else {
      localStorage.removeItem('pulseiq_user');
      localStorage.removeItem('token');
    }
  };

  useEffect(() => {
    const storedUser = localStorage.getItem('pulseiq_user');
    const token = localStorage.getItem('token');
    
    if (storedUser && token) {
      updateUser(JSON.parse(storedUser));
    } else {
      updateUser(null);
    }
    setIsLoading(false);
  }, []);

  const login = async (credentials: { identifier: string; password: string }): Promise<boolean> => {
    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8085/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
        credentials: 'include'
      });

      if (!response.ok) throw new Error('Login failed');

      const data = await response.json();
      const userRole = (data.role || '').toLowerCase();

      if (!['admin', 'doctor', 'patient', 'technician'].includes(userRole)) {
        throw new Error('Invalid role received');
      }

      const loggedUser: User = {
        id: data.userId,
        email: data.email,
        phone: data.phone,
        role: userRole as User['role'],
        name: `${userRole.charAt(0).toUpperCase() + userRole.slice(1)} User`,
        status: data.status || 'approved'
      };

      localStorage.setItem('token', data.token);
      updateUser(loggedUser);
      return true;
    } catch (err) {
      console.error("Login failed:", err);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    updateUser(null);
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      isLoading, 
      setUser: updateUser
    }}>
      {!isLoading && children}
    </AuthContext.Provider>
  );
};

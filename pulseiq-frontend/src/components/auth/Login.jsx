import React, { useState } from 'react';
import { User, Lock, Heart } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Button from '../ui/Button';

const Login = ({ onSwitchToRegister }) => {
  const { login, loading } = useAuth();
  const [formData, setFormData] = useState({
    userId: '',
    password: '',
    userType: ''
  });
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);

  const userTypes = [
    { value: 'PATIENT', label: 'Patient' },
    { value: 'DOCTOR', label: 'Doctor' },
    { value: 'TECHNICIAN', label: 'Technician' },
    { value: 'ADMIN', label: 'Admin' }
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});

    if (!formData.userId || !formData.password || !formData.userType) {
      setErrors({ general: 'All fields are required' });
      return;
    }

    const result = await login(formData);
    if (!result.success) {
      setErrors({ general: result.error });
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: '' }));
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-8 border border-gray-100">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl mx-auto mb-4 flex items-center justify-center">
            <Heart className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gray-800 mb-2">PulseIQ</h1>
          <p className="text-gray-600">Hospital Management System</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <Input
            icon={User}
            placeholder="User ID"
            value={formData.userId}
            onChange={(e) => handleChange('userId', e.target.value)}
            error={errors.userId}
          />

          <Select
            icon={User}
            options={userTypes}
            value={formData.userType}
            onChange={(e) => handleChange('userType', e.target.value)}
            placeholder="Select User Type"
            error={errors.userType}
          />

          <Input
            icon={Lock}
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={(e) => handleChange('password', e.target.value)}
            error={errors.password}
            showPassword={showPassword}
            onTogglePassword={() => setShowPassword(!showPassword)}
          />

          {errors.general && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl">
              {errors.general}
            </div>
          )}

          <Button
            type="submit"
            size="lg"
            loading={loading}
            className="w-full"
          >
            Sign In
          </Button>
        </form>

        <div className="mt-8 text-center">
          <p className="text-gray-600">
            Don't have an account?{' '}
            <button
              onClick={onSwitchToRegister}
              className="text-blue-600 hover:text-blue-700 font-semibold"
            >
              Register here
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
import React, { useState } from 'react';
import { User, Lock, Mail, Phone, Calendar, Heart, Building, GraduationCap, UserCheck, Stethoscope, Settings } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Button from '../ui/Button';

const Registration = ({ onSwitchToLogin }) => {
  const { register, loading } = useAuth();
  const [currentStep, setCurrentStep] = useState('type');
  const [userType, setUserType] = useState('');
  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [success, setSuccess] = useState('');

  const userTypes = [
    { value: 'PATIENT', label: 'Patient', icon: Heart, color: 'from-pink-500 to-rose-500' },
    { value: 'DOCTOR', label: 'Doctor', icon: Stethoscope, color: 'from-blue-500 to-cyan-500' },
    { value: 'TECHNICIAN', label: 'Technician', icon: Settings, color: 'from-green-500 to-emerald-500' },
    { value: 'ADMIN', label: 'Admin', icon: UserCheck, color: 'from-purple-500 to-violet-500' }
  ];

  const genderOptions = [
    { value: 'Male', label: 'Male' },
    { value: 'Female', label: 'Female' },
    { value: 'Other', label: 'Other' }
  ];

  const bloodGroupOptions = [
    { value: 'A_POSITIVE', label: 'A+' },
    { value: 'A_NEGATIVE', label: 'A-' },
    { value: 'B_POSITIVE', label: 'B+' },
    { value: 'B_NEGATIVE', label: 'B-' },
    { value: 'AB_POSITIVE', label: 'AB+' },
    { value: 'AB_NEGATIVE', label: 'AB-' },
    { value: 'O_POSITIVE', label: 'O+' },
    { value: 'O_NEGATIVE', label: 'O-' }
  ];

  const handleUserTypeSelect = (type) => {
    setUserType(type);
    setCurrentStep('form');
    setFormData({});
    setErrors({});
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: '' }));
  };

  const validateForm = () => {
    const newErrors = {};

    // Common validation
    if (!formData.firstName) newErrors.firstName = 'First name is required';
    if (!formData.lastName) newErrors.lastName = 'Last name is required';
    if (!formData.email) newErrors.email = 'Email is required';
    if (!formData.phone) newErrors.phone = 'Phone is required';
    if (!formData.password) newErrors.password = 'Password is required';
    if (formData.password && formData.password.length < 6) newErrors.password = 'Password must be at least 6 characters';

    // User type specific validation
    if (userType === 'PATIENT') {
      if (!formData.patientId) newErrors.patientId = 'Patient ID is required';
      if (!formData.age) newErrors.age = 'Age is required';
      if (!formData.gender) newErrors.gender = 'Gender is required';
    } else {
      if (!formData[`${userType.toLowerCase()}Id`]) newErrors[`${userType.toLowerCase()}Id`] = `${userType} ID is required`;
      if (!formData.hospitalId) newErrors.hospitalId = 'Hospital ID is required';
      if (!formData.specialization) newErrors.specialization = 'Specialization is required';

      if (userType === 'DOCTOR') {
        if (!formData.degree) newErrors.degree = 'Degree is required';
        if (!formData.licenseNumber) newErrors.licenseNumber = 'License number is required';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccess('');

    if (!validateForm()) return;

    const result = await register(formData, userType);
    if (result.success) {
      setSuccess(result.message);
      setTimeout(() => {
        onSwitchToLogin();
      }, 2000);
    } else {
      setErrors({ general: result.error });
    }
  };

  const renderUserTypeSelection = () => (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 flex items-center justify-center p-4">
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-2xl p-8 border border-gray-100">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl mx-auto mb-4 flex items-center justify-center">
            <Heart className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Join PulseIQ</h1>
          <p className="text-gray-600">Choose your account type</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
          {userTypes.map((type) => {
            const Icon = type.icon;
            return (
              <button
                key={type.value}
                onClick={() => handleUserTypeSelect(type.value)}
                className="p-6 border-2 border-gray-200 rounded-xl hover:border-blue-500 transition-all duration-200 group"
              >
                <div className={`w-12 h-12 bg-gradient-to-r ${type.color} rounded-xl mx-auto mb-3 flex items-center justify-center group-hover:scale-110 transition-transform duration-200`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
                <h3 className="text-lg font-semibold text-gray-800">{type.label}</h3>
              </button>
            );
          })}
        </div>

        <div className="text-center">
          <p className="text-gray-600">
            Already have an account?{' '}
            <button
              onClick={onSwitchToLogin}
              className="text-blue-600 hover:text-blue-700 font-semibold"
            >
              Sign in
            </button>
          </p>
        </div>
      </div>
    </div>
  );

  const renderRegistrationForm = () => (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 flex items-center justify-center p-4">
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-2xl p-8 border border-gray-100">
        <div className="text-center mb-8">
          <button
            onClick={() => setCurrentStep('type')}
            className="text-blue-600 hover:text-blue-700 font-semibold mb-4"
          >
            ← Back to user type selection
          </button>
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Register as {userType}</h1>
        </div>

        {success && (
          <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-xl mb-6">
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* ID Field */}
            <Input
              icon={User}
              placeholder={`${userType} ID`}
              value={formData[`${userType.toLowerCase()}Id`] || ''}
              onChange={(e) => handleChange(`${userType.toLowerCase()}Id`, e.target.value)}
              error={errors[`${userType.toLowerCase()}Id`]}
            />

            {/* Hospital ID for non-patients */}
            {userType !== 'PATIENT' && (
              <Input
                icon={Building}
                placeholder="Hospital ID"
                type="number"
                value={formData.hospitalId || ''}
                onChange={(e) => handleChange('hospitalId', parseInt(e.target.value))}
                error={errors.hospitalId}
              />
            )}

            {/* Name Fields */}
            <Input
              icon={User}
              placeholder="First Name"
              value={formData.firstName || ''}
              onChange={(e) => handleChange('firstName', e.target.value)}
              error={errors.firstName}
            />

            <Input
              icon={User}
              placeholder="Last Name"
              value={formData.lastName || ''}
              onChange={(e) => handleChange('lastName', e.target.value)}
              error={errors.lastName}
            />

            {/* Contact Fields */}
            <Input
              icon={Mail}
              type="email"
              placeholder="Email"
              value={formData.email || ''}
              onChange={(e) => handleChange('email', e.target.value)}
              error={errors.email}
            />

            <Input
              icon={Phone}
              placeholder="Phone (01XXXXXXXXX)"
              value={formData.phone || ''}
              onChange={(e) => handleChange('phone', e.target.value)}
              error={errors.phone}
            />

            {/* Password */}
            <div className="md:col-span-2">
              <Input
                icon={Lock}
                type="password"
                placeholder="Password"
                value={formData.password || ''}
                onChange={(e) => handleChange('password', e.target.value)}
                error={errors.password}
                showPassword={showPassword}
                onTogglePassword={() => setShowPassword(!showPassword)}
              />
            </div>

            {/* Patient-specific fields */}
            {userType === 'PATIENT' && (
              <>
                <Input
                  icon={Calendar}
                  type="number"
                  placeholder="Age"
                  value={formData.age || ''}
                  onChange={(e) => handleChange('age', parseInt(e.target.value))}
                  error={errors.age}
                />

                <Select
                  icon={User}
                  options={genderOptions}
                  value={formData.gender || ''}
                  onChange={(e) => handleChange('gender', e.target.value)}
                  placeholder="Select Gender"
                  error={errors.gender}
                />

                <Select
                  icon={Heart}
                  options={bloodGroupOptions}
                  value={formData.bloodGroup || ''}
                  onChange={(e) => handleChange('bloodGroup', e.target.value)}
                  placeholder="Blood Group (Optional)"
                  error={errors.bloodGroup}
                />
              </>
            )}

            {/* Non-patient fields */}
            {userType !== 'PATIENT' && (
              <div className="md:col-span-2">
                <Input
                  icon={Stethoscope}
                  placeholder="Specialization"
                  value={formData.specialization || ''}
                  onChange={(e) => handleChange('specialization', e.target.value)}
                  error={errors.specialization}
                />
              </div>
            )}

            {/* Doctor-specific fields */}
            {userType === 'DOCTOR' && (
              <>
                <Input
                  icon={GraduationCap}
                  placeholder="Degree"
                  value={formData.degree || ''}
                  onChange={(e) => handleChange('degree', e.target.value)}
                  error={errors.degree}
                />

                <Input
                  icon={UserCheck}
                  placeholder="License Number"
                  value={formData.licenseNumber || ''}
                  onChange={(e) => handleChange('licenseNumber', e.target.value)}
                  error={errors.licenseNumber}
                />

                <Input
                  icon={User}
                  placeholder="Assistant Name (Optional)"
                  value={formData.assistantName || ''}
                  onChange={(e) => handleChange('assistantName', e.target.value)}
                />

                <Input
                  icon={Phone}
                  placeholder="Assistant Number (Optional)"
                  value={formData.assistantNumber || ''}
                  onChange={(e) => handleChange('assistantNumber', e.target.value)}
                />

                <div className="md:col-span-2">
                  <Input
                    icon={Calendar}
                    type="number"
                    placeholder="Consultation Fee"
                    value={formData.consultationFee || ''}
                    onChange={(e) => handleChange('consultationFee', parseFloat(e.target.value))}
                    error={errors.consultationFee}
                  />
                </div>
              </>
            )}
          </div>

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
            Register
          </Button>
        </form>
      </div>
    </div>
  );

  return currentStep === 'type' ? renderUserTypeSelection() : renderRegistrationForm();
};

export default Registration;
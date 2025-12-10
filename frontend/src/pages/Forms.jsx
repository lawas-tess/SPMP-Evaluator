// RegisterForm.jsx
import { TextInput, PasswordInput, RoleSelect } from "./FormFields";
import SocialLogin from "./SocialLogin";

export const RegisterForm = ({
  formData,
  handleInputChange,
  handleSubmit,
  isProcessing,
  showPassword,
  setShowPassword,
}) => (
  <form onSubmit={handleSubmit} className="space-y-5">
    <div className="grid grid-cols-2 gap-4">
      <TextInput
        label="First Name"
        name="firstName"
        value={formData.firstName}
        onChange={handleInputChange}
        placeholder="John"
      />
      <TextInput
        label="Last Name"
        name="lastName"
        value={formData.lastName}
        onChange={handleInputChange}
        placeholder="Doe"
      />
    </div>

    <div className="grid grid-cols-2 gap-4">
      <TextInput
        label="Email"
        name="email"
        value={formData.email}
        onChange={handleInputChange}
        placeholder="you@example.com"
      />
      <RoleSelect value={formData.role} onChange={handleInputChange} />
    </div>

    <PasswordInput
      value={formData.password}
      onChange={handleInputChange}
      show={showPassword}
      toggle={() => setShowPassword(!showPassword)}
    />

    <button
      type="submit"
      disabled={isProcessing}
      className="w-full bg-gradient-to-r from-accent to-accent-two hover:from-accent-two hover:to-accent-three text-white font-semibold py-3 rounded-full transition transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
    >
      {isProcessing ? "Processing..." : "Create Account"}
    </button>

    <SocialLogin isLogin={false} />
  </form>
);

export const LoginForm = ({
  formData,
  handleInputChange,
  handleSubmit,
  isProcessing,
  showPassword,
  setShowPassword,
}) => (
  <form onSubmit={handleSubmit} className="space-y-3">
    <TextInput
      label="Username or Email"
      name="username"
      value={formData.username}
      onChange={handleInputChange}
      placeholder="Enter your username"
    />

    <PasswordInput
      value={formData.password}
      onChange={handleInputChange}
      show={showPassword}
      toggle={() => setShowPassword(!showPassword)}
    />

    <div className="flex justify-end">
      <a
        href="#"
        className={`text-sm text-accent hover:text-accent-three font-medium`}
      >
        Forgot password?
      </a>
    </div>

    <button
      type="submit"
      disabled={isProcessing}
      className="w-full bg-gradient-to-r from-accent to-accent-two hover:from-accent-two hover:to-accent-three text-white font-semibold py-3 rounded-full transition transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
    >
      {isProcessing ? "Processing..." : "Login"}
    </button>

    <SocialLogin isLogin={true} />
  </form>
);

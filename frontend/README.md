# SPMP Evaluator - Frontend

A modern React-based frontend for the SPMP Evaluator application with authentication, role-based access control, and a beautiful purple gradient design.

## Features

- **User Authentication**: Login and registration with role selection
- **Responsive Design**: Mobile-friendly layout using Tailwind CSS
- **JWT Token Management**: Secure API communication with automatic token handling
- **Protected Routes**: Role-based route protection
- **Modern UI**: Purple gradient design matching the provided design mockup
- **Error Handling**: Comprehensive error messages and validation

## Tech Stack

- **React 18.3** - UI library
- **React Router v6** - Navigation and routing
- **Axios** - HTTP client
- **Tailwind CSS** - Styling framework
- **React Icons** - Icon components

## Prerequisites

- Node.js 16+ 
- npm or yarn
- Backend API running on `http://localhost:8080` (or configured in `.env`)

## Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Create a `.env` file (or update the existing one) with your API base URL:
```
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## Running the Application

### Development Mode

```bash
npm run dev
# or
npm start
```

The application will start on `http://localhost:3000`

### Production Build

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

## Project Structure

```
frontend/
├── public/
│   └── index.html          # Main HTML file
├── src/
│   ├── components/
│   │   └── ProtectedRoute.jsx    # Route protection component
│   ├── context/
│   │   └── AuthContext.js        # Authentication context
│   ├── hooks/
│   │   └── useAuthHook.js        # Custom auth hook
│   ├── pages/
│   │   ├── AuthPage.jsx          # Login/Signup page
│   │   └── Dashboard.jsx         # Dashboard page
│   ├── services/
│   │   └── apiService.js         # API calls and axios config
│   ├── App.jsx                   # Main app component
│   ├── App.css                   # App styles
│   ├── index.jsx                 # React entry point
│   └── index.css                 # Global styles
├── .env                    # Environment variables
├── .env.example            # Example env file
├── tailwind.config.js      # Tailwind CSS config
├── postcss.config.js       # PostCSS config
├── package.json            # Dependencies and scripts
└── README.md              # This file
```

## Features & Pages

### Authentication (AuthPage)

- **Toggle between Login and Signup**: Switch seamlessly between login and registration forms
- **Login Form**: Username and password authentication
- **Registration Form**: 
  - First name and last name
  - Email address
  - Username selection
  - Password (8+ characters)
  - Role selection (Student, Professor, Project Manager)
- **Social Login**: Placeholders for Facebook, LinkedIn, and Google OAuth
- **Error Handling**: Clear error messages for failed login/registration
- **Password Visibility Toggle**: Eye icon to show/hide password

### Dashboard

- Welcome message with user's first name
- Display user information
- Logout functionality
- Placeholder for future dashboard content

## API Integration

### Authentication Endpoints Used

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user and receive JWT token
- `GET /api/auth/validate` - Validate JWT token
- `GET /api/auth/health` - Health check

### Auth Context

The `AuthContext` provides:
- `user` - Current logged-in user object
- `token` - JWT authentication token
- `isAuthenticated` - Boolean indicating authentication status
- `loading` - Loading state during auth checks
- `error` - Error messages
- `login()` - Login function
- `register()` - Registration function
- `logout()` - Logout function
- `setError()` - Set custom error message

### API Service

Axios instance with automatic:
- Bearer token attachment to all requests
- 401 error handling (redirects to login if token expires)
- Customizable base URL via environment variable

## Styling

The application uses Tailwind CSS for styling with custom configuration:

- **Primary Color**: Purple (`#7C3AED`)
- **Gradient Background**: Purple to blue gradient
- **Responsive Breakpoints**: Mobile-first approach
- **Animated Shapes**: Floating blob animations in background

## Customization

### Change API Base URL

Edit `.env` file:
```
REACT_APP_API_BASE_URL=https://your-api-domain.com/api
```

### Update Colors

Edit `tailwind.config.js`:
```js
theme: {
  extend: {
    colors: {
      primary: '#YOUR_COLOR',
    }
  }
}
```

### Add New Pages

1. Create file in `src/pages/`
2. Add route in `src/App.jsx`
3. Use `<ProtectedRoute>` if authentication required

## Error Handling

The application automatically handles:
- Network errors with user-friendly messages
- Invalid credentials with specific feedback
- Token expiration (automatic redirect to login)
- Validation errors on form submission
- HTTP error responses

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Performance

- Code splitting with React Router
- Lazy loading for routes
- Optimized assets with Tailwind CSS
- Minimal bundle size with tree-shaking

## Security

- JWT tokens stored in localStorage
- HTTP-only cookie option available (upgrade recommended)
- Automatic token attachment to API calls
- Protected routes require authentication
- Secure password field with visibility toggle

## Future Enhancements

- [ ] Password reset functionality
- [ ] OAuth integration (Google, GitHub, LinkedIn)
- [ ] Two-factor authentication
- [ ] User profile management
- [ ] Document upload interface
- [ ] Evaluation results dashboard
- [ ] Task management UI
- [ ] Reporting and analytics

## Troubleshooting

### Blank page on startup
- Check browser console for errors
- Verify `.env` file has correct API URL
- Ensure backend API is running

### 401 Unauthorized errors
- Token may be expired, login again
- Check backend JWT secret matches configuration

### CORS errors
- Ensure backend has CORS enabled
- Check API base URL in `.env`

### Styling not applied
- Rebuild Tailwind CSS: `npm run build`
- Clear browser cache
- Restart dev server

## Support

For issues or questions:
1. Check browser console for error details
2. Verify backend API is running
3. Check `.env` configuration
4. Review network requests in DevTools

## License

This project is part of the SPMP Evaluator capstone project.

import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import RouterConfig from './routes/RouterConfig';
import './App.css';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <RouterConfig />
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
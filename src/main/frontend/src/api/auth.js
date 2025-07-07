// frontend/api/auth.js

export async function login(username, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            // Only try to parse JSON if it exists
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || 'Login failed');
        }

        const data = await response.json();  // ✅ only once!
        const token = data.token;

        if (!token || token.includes(':') || token.includes('"')) {
            throw new Error('Invalid token format received');
        }

        localStorage.setItem('token', token);
        return true;

    } catch (error) {
        throw new Error(error.message || "Login failed");
    }
}

export async function register(username, password) {
    try {
        const response = await fetch("/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Registration failed");
        }

        return await response.text(); // return message from backend
    } catch (error) {
        throw new Error(error.message || "Registration failed");
    }
}

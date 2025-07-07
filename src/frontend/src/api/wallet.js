const API_BASE = '/api';

function headers() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    };
}

export async function createWallet(currency, name) {
    const res = await fetch(`${API_BASE}/wallets`, {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify({ currency, name })
    });

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || 'Failed to create wallet');
    }

    return await res.json();
}

export async function loadFunds(walletId, amount, currency, description, variableSymbol) {
    const res = await fetch(`${API_BASE}/wallets/operation`, {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify({
            walletId,
            amount,
            currency, // 🔁 dynamic
            transactionType: 'INCOME',
            description,
            variableSymbol
        })
    });

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || 'Failed to load funds');
    }

    return await res.json();
}

export async function withdrawFunds(walletId, amount, currency, bankAccountNumber, description, variableSymbol) {
    const res = await fetch(`${API_BASE}/wallets/operation`, {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify({
            walletId,
            amount,
            currency, // 🔁 dynamic
            transactionType: 'WITHDRAWAL',
            bankAccountNumber,
            description,
            variableSymbol
        })
    });

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || 'Failed to withdraw funds');
    }

    return await res.json();
}

export async function transferFunds(walletId, receiverWalletId, amount, currency, description, variableSymbol) {
    const res = await fetch(`${API_BASE}/wallets/operation`, {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify({
            walletId,
            receiverWalletId,
            amount,
            currency, // 🔁 dynamic
            transactionType: 'TRANSFER',
            description,
            variableSymbol
        })
    });

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || 'Failed to transfer funds');
    }

    return await res.json();
}

export async function generateQr(transactionId) {
    if (!transactionId) throw new Error("Missing transaction ID");

    const res = await fetch(`${API_BASE}/transactions/qr/${transactionId}`, {
        headers: headers()
    });

    if (!res.ok) {
        const err = await res.text().catch(() => '');
        throw new Error(err || 'Failed to generate QR code');
    }

    const base64 = await res.text();
    return `data:image/png;base64,${base64}`;
}

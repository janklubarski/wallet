import { register, login } from './api/auth.js';
import {
    createWallet,
    loadFunds,
    withdrawFunds,
    transferFunds,
    generateQr
} from './api/wallet.js';

const token = localStorage.getItem('token');

document.addEventListener('DOMContentLoaded', () => {
    setupDarkModeToggle();

    const isDashboard = window.location.pathname.includes('dashboard');
    if (isDashboard) {
        if (!token) {
            window.location.href = '/pages/login.html';
        } else {
            loadWallets();
            setupDashboardHandlers();
        }
    }

    document.getElementById('register-btn')?.addEventListener('click', async () => {
        const username = document.getElementById('reg-username').value;
        const password = document.getElementById('reg-password').value;
        try {
            await register(username, password);
            alert('Registration successful. Please log in.');
            window.location.href = '/pages/login.html';
        } catch (err) {
            document.getElementById('register-error').innerText = err.message;
        }
    });

    document.getElementById('login-btn')?.addEventListener('click', async () => {
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;
        try {
            await login(username, password);
            window.location.href = '/pages/dashboard.html';
        } catch (err) {
            document.getElementById('login-error').innerText = err.message;
        }
    });
});

function setupDarkModeToggle() {
    const toggle = document.getElementById('dark-mode-toggle');
    const isDark = localStorage.getItem('dark-mode') === 'true';
    if (isDark) document.body.classList.add('dark-mode');

    if (toggle) {
        toggle.innerText = isDark ? 'Light Mode' : 'Dark Mode';
        toggle.addEventListener('click', () => {
            document.body.classList.toggle('dark-mode');
            const nowDark = document.body.classList.contains('dark-mode');
            localStorage.setItem('dark-mode', nowDark);
            toggle.innerText = nowDark ? 'Light Mode' : 'Dark Mode';
        });
    }
}

async function loadWallets() {
    const res = await fetch('/api/wallets', {
        headers: { Authorization: `Bearer ${token}` }
    });

    const container = document.getElementById('wallets-list');
    const fundsSelect = document.getElementById('funds-wallet-select');
    const withdrawSelect = document.getElementById('withdraw-wallet-select');
    const transferSelect = document.getElementById('transfer-wallet-select');
    const actionsSection = document.getElementById('wallet-actions');

    container.innerHTML = '';
    if (fundsSelect) fundsSelect.innerHTML = '';
    if (withdrawSelect) withdrawSelect.innerHTML = '';
    if (transferSelect) transferSelect.innerHTML = '';

    if (!res.ok) {
        container.innerText = 'Failed to load wallets.';
        actionsSection.style.display = 'none';
        return;
    }

    const wallets = await res.json();

    if (wallets.length === 0) {
        container.innerText = 'You have no wallet yet.';
        actionsSection.style.display = 'none';
        return;
    }

    actionsSection.style.display = 'block';

    wallets.forEach((wallet) => {
        const { id, currency, balance, walletName } = wallet;

        const walletDiv = document.createElement('div');
        walletDiv.classList.add('wallet-entry');

        // Wallet Info
        const p = document.createElement('p');
        p.innerText = `Wallet id: ${id} | ${walletName} | ${currency} | Balance: ${balance}`;
        walletDiv.appendChild(p);

        // Date range inputs
        const dateFromInput = document.createElement('input');
        dateFromInput.type = 'date';
        dateFromInput.id = `from-${id}`;

        const dateToInput = document.createElement('input');
        dateToInput.type = 'date';
        dateToInput.id = `to-${id}`;

        walletDiv.appendChild(dateFromInput);
        walletDiv.appendChild(dateToInput);

        // Show history button
        const button = document.createElement('button');
        button.innerText = 'Show Transaction History';
        button.onclick = () => {
            const historySection = document.getElementById(`history-${id}`);
            const isVisible = historySection.style.display === 'block';

            if (isVisible) {
                historySection.style.display = 'none';
                button.innerText = 'Show Transaction History';
            } else {
                historySection.style.display = 'block';
                loadTransactionHistory(id);
                button.innerText = 'Hide Transaction History';
            }
        };
        walletDiv.appendChild(button);

        // History container
        const historyDiv = document.createElement('div');
        historyDiv.id = `history-${id}`;
        historyDiv.style.display = 'none';
        walletDiv.appendChild(historyDiv);

        container.appendChild(walletDiv);

        // Add to dropdowns as before
        const label = `${walletName} (#${id}) - ${currency}`;
        const option = new Option(label, id);
        option.dataset.currency = currency;

        fundsSelect?.appendChild(option.cloneNode(true));
        withdrawSelect?.appendChild(option.cloneNode(true));
        transferSelect?.appendChild(option.cloneNode(true));
    });
}


function getSelectedCurrency(selectElementId) {
    const select = document.getElementById(selectElementId);
    const selectedOption = select?.options[select.selectedIndex];
    return selectedOption?.dataset.currency || 'EUR'; // fallback
}

function setupDashboardHandlers() {
    document.getElementById('create-wallet-btn')?.addEventListener('click', async () => {
        const currency = document.getElementById('create-currency').value;
        const name = document.getElementById('wallet-name').value.trim();
        const errorEl = document.getElementById('wallet-error');
        errorEl.textContent = '';

        if (name.length < 3 || name.length > 100) {
            errorEl.textContent = 'Wallet name must be between 3 and 100 characters.';
            return;
        }

        try {
            await createWallet(currency, name);
            await loadWallets();
            document.getElementById('wallet-name').value = '';
        } catch (err) {
            errorEl.textContent = err.message || 'Failed to create wallet';
        }
    });

    document.getElementById('load-funds-btn')?.addEventListener('click', async () => {
        const walletSelect = document.getElementById('funds-wallet-select');
        const walletId = walletSelect.value;
        const currency = walletSelect.options[walletSelect.selectedIndex]?.dataset.currency || 'EUR'; //EUR is default;
        const amount = document.getElementById('funds-amount').value;
        const description = document.getElementById('funds-description').value;
        const variableSymbol = document.getElementById('funds-symbol').value;
        const errorEl = document.getElementById('funds-error');
        // const img = document.getElementById('funds-qr');
        errorEl.textContent = '';
        // img.style.display = 'none';

        try {
            const tx = await loadFunds(walletId, parseFloat(amount), currency, description, variableSymbol);
            // - qr placeholder const qr = await generateQr(tx.id);
            showConfirmButton(tx.id, 'funds-confirm');
            // img.src = qr;
            // img.style.display = 'block';
        } catch (err) {
            errorEl.textContent = err.message || 'Failed to load funds';
        }
    });

    document.getElementById('withdraw-btn')?.addEventListener('click', async () => {
        const walletId = document.getElementById('withdraw-wallet-select').value;
        const amount = document.getElementById('withdraw-amount').value;
        const bankAccount = document.getElementById('bank-account').value;
        const description = document.getElementById('withdraw-description').value;
        const variableSymbol = document.getElementById('withdraw-symbol').value;
        const currency = getSelectedCurrency('withdraw-wallet-select');
        const errorEl = document.getElementById('withdraw-error');
        // const img = document.getElementById('withdraw-qr');
        errorEl.textContent = '';
        // img.style.display = 'none';

        try {
            const tx = await withdrawFunds(walletId, parseFloat(amount), currency, bankAccount, description, variableSymbol);
            // - qr placeholder const qr = await generateQr(tx.id);
            showConfirmButton(tx.id, 'withdraw-confirm');
            // img.src = qr;
            // img.style.display = 'block';
        } catch (err) {
            errorEl.textContent = err.message || 'Failed to withdraw funds';
        }
    });

    document.getElementById('transfer-btn')?.addEventListener('click', async () => {
        const walletId = document.getElementById('transfer-wallet-select').value;
        const receiverWalletId = document.getElementById('receiver-wallet-id').value;
        const amount = document.getElementById('transfer-amount').value;
        const description = document.getElementById('transfer-description').value;
        const variableSymbol = document.getElementById('transfer-symbol').value;
        const currency = getSelectedCurrency('transfer-wallet-select');
        const errorEl = document.getElementById('transfer-error');
        // const img = document.getElementById('transfer-qr');
        errorEl.textContent = '';
        // img.style.display = 'none';

        try {
            const tx = await transferFunds(walletId, receiverWalletId, parseFloat(amount), currency, description, variableSymbol);
            // - qr placeholder const qr = await generateQr(tx.id);
            showConfirmButton(tx.id, 'transfer-confirm');
            // img.src = qr;
            // img.style.display = 'block';
        } catch (err) {
            errorEl.textContent = err.message || 'Failed to transfer funds';
        }
    });

    function showConfirmButton(transactionId, containerId) {
        const container = document.getElementById(containerId);
        container.innerHTML = ''; // clear previous content

        const button = document.createElement('button');
        button.innerText = 'Confirm Transaction';
        button.onclick = async () => {
            try {
                const res = await fetch(`/api/transactions/confirm?transactionId=${transactionId}&confirm=true`, {
                    method: 'POST'
                });
                const msg = await res.text();

                // ✅ Update UI
                container.innerHTML = '';
                const confirmedText = document.createElement('p');
                confirmedText.innerText = '✅ Transaction has been confirmed.';
                confirmedText.style.color = 'green';
                container.appendChild(confirmedText);

                await loadWallets(); // refresh wallet balances
            } catch (err) {
                alert('❌ Failed to confirm transaction.');
            }
        };

        container.appendChild(button);
    }
}

async function loadTransactionHistory(walletId) {
    const from = document.getElementById(`from-${walletId}`).value;
    const to = document.getElementById(`to-${walletId}`).value;
    const container = document.getElementById(`history-${walletId}`);
    container.innerHTML = 'Loading...';

    let url = `/api/transactions/history/${walletId}`;
    const params = [];

    if (from) params.push(`dateFrom=${from}T00:00:00`);
    if (to) params.push(`dateTo=${to}T23:59:59`);

    if (params.length > 0) {
        url += '?' + params.join('&');
    }

    try {
        const res = await fetch(url, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) {
            throw new Error('Failed to fetch transactions');
        }

        const transactions = await res.json();

        if (transactions.length === 0) {
            container.innerHTML = '<p>No transactions found.</p>';
            return;
        }

        // Render table
        const table = document.createElement('table');
        table.classList.add('transaction-table');

        const thead = document.createElement('thead');
        thead.innerHTML = `
      <tr>
        <th>Date</th><th>Amount</th><th>Type</th><th>Status</th><th>Description</th><th>Symbol</th>
      </tr>`;
        table.appendChild(thead);

        const tbody = document.createElement('tbody');

        transactions.forEach(tx => {
            const row = document.createElement('tr');
            row.innerHTML = `
        <td>${tx.transactionDate}</td>
        <td>${tx.amount}</td>
        <td>${tx.transactionType}</td>
        <td>${tx.status}</td>
        <td>${tx.description || ''}</td>
        <td>${tx.variableSymbol || ''}</td>`;
            tbody.appendChild(row);
        });

        table.appendChild(tbody);
        container.innerHTML = '';
        container.appendChild(table);

    } catch (err) {
        container.innerHTML = `<p style="color: red;">${err.message}</p>`;
    }
}


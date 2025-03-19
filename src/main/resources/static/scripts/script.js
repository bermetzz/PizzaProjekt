
function updatePrice(radioInput) {
    const container = radioInput.closest('.pizza-size-selector');
    const prices = JSON.parse(container.getAttribute('data-prices'));
    const selectedPriceIndex = parseInt(radioInput.value, 10);
    const selectedPrice = prices[selectedPriceIndex];

    const priceDisplay = container.parentElement.querySelector('.price-display');
    priceDisplay.textContent = `In den Warenkorb ${selectedPrice} som`;
}

if (!localStorage.getItem('cart')) {
    localStorage.setItem('cart', JSON.stringify([]));
}

function addToCart(button) {
    const pizzaContainer = button.closest('.item-card__container');
    const pizzaName = pizzaContainer.querySelector('.item-card__name').textContent.trim();
    const pizzaImage = pizzaContainer.querySelector('img').src;
    const priceSelector = pizzaContainer.querySelector('.pizza-size-selector');
    const prices = JSON.parse(priceSelector.getAttribute('data-prices'));
    const selectedSizeIndex = priceSelector.querySelector('input:checked').value;
    const selectedPrice = prices[selectedSizeIndex];
    const size = ['SMALL', 'MEDIUM', 'LARGE'][selectedSizeIndex];
    const pizzaId = priceSelector.getAttribute('data-id') || pizzaContainer.getAttribute('data-id');
    const isUserLoggedIn = document.body.getAttribute('data-logged-in') === 'true';

    const item = {
        pizzaId: pizzaId,
        name: pizzaName,
        image: pizzaImage,
        size: size,
        price: selectedPrice,
        quantity: 1,
    };

    if (!isUserLoggedIn) {
        // Save to LocalStorage
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        const existingItem = cart.find(i => i.pizzaId === pizzaId && i.size === size);
        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push(item);
        }
        localStorage.setItem('cart', JSON.stringify(cart));
        updateCartSidebar(); // Update the sidebar immediately for guests
    } else {
        // Save to Database and fetch updated cart
        fetch('/cart/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(item),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to add item to the cart.');
                }
                console.log('Item added to the database cart');
                return fetch('/cart'); // Fetch the updated cart
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch updated cart items.');
                }
                return response.json();
            })
            .then(cartItems => {
                populateCartSidebar(cartItems); // Update the sidebar for logged-in users
            })
            .catch(error => console.error('Error updating cart:', error));
    }
}





function updateCartSidebar() {
    const isUserLoggedIn = document.body.getAttribute('data-logged-in') === 'true';
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const sidebar = document.querySelector('.offcanvas-body.cart');
    const cartProductContainer = sidebar.querySelector('.cart__product-container');
    const totalSumElement = sidebar.querySelector('.cart-total-sum');
    const productCountElement = sidebar.querySelector('.cart-product-count');
    const cartOpenButton = document.querySelector('#cart-qty');

    cartProductContainer.innerHTML = '';
    let totalSum = 0;
    let totalQuantity = 0;

    cart.forEach(item => {
        totalSum += item.price * item.quantity;
        totalQuantity += item.quantity;

        const productHTML = `
                <div class="cart__product d-flex gap-3 py-3 border-bottom">
                    <img class="cart__product-image" src="${item.image}" alt="photo of pizza" />
                    <div class="flex-grow-1">
                        <p class="fw-bold mb-1">${item.name}</p>
                        <p class="text-muted mb-2">${item.size}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="fw-bold">${(item.price * item.quantity).toFixed(2)} som</span>
                            <div class="d-flex align-items-center">
                                <button class="btn btn-counter btn-outline-secondary rounded-circle"
                                    onclick="updateQuantity('${item.pizzaId}', '${item.size}', -1)">-</button>
                                <span class="px-2">${item.quantity}</span>
                                <button class="btn btn-counter btn-outline-secondary rounded-circle"
                                    onclick="updateQuantity('${item.pizzaId}', '${item.size}', 1)">+</button>
                            </div>
                        </div>
                    </div>
                </div>`;
        cartProductContainer.innerHTML += productHTML;
    });

    totalSumElement.textContent = `Summe der Bestellung: ${totalSum.toFixed(2)} som;`;
    productCountElement.textContent = `${totalQuantity} Produkte für ${totalSum.toFixed(2)} som;`;
    cartOpenButton.textContent = `${totalQuantity}`;

    console.log("update side bar")
}


function updateQuantity(pizzaId, size, delta) {
    const isUserLoggedIn = document.body.getAttribute('data-logged-in') === 'true';

    if (!isUserLoggedIn) {
        // Handle LocalStorage logic for guest users
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        const item = cart.find(item => item.pizzaId === pizzaId && item.size === size);

        if (item) {
            item.quantity += delta;
            if (item.quantity <= 0) {
                const index = cart.indexOf(item);
                cart.splice(index, 1);
            }
        }

        localStorage.setItem('cart', JSON.stringify(cart));
        updateCartSidebar();
    } else {
        // Send update to server for logged-in users
        const payload = { pizzaId, size, delta };

        fetch('/cart/updateQuantity', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to update quantity.');
                }
                return response.json();
            })
            .then(updatedCart => {
                populateCartSidebar(updatedCart); // Update sidebar with updated cart
                console.log("updated cart: "+JSON.stringify(updatedCart))
            })
            .catch(error => console.error('Error updating quantity:', error));
    }
}

function syncCartToDatabase(items = null, clearLocalStorage = true) {
    // If no items provided, sync the entire cart from localStorage
    const cart = items || JSON.parse(localStorage.getItem('cart')) || [];

    if (cart.length === 0) {
        console.log('No items to sync.');
        return;
    }

    fetch('/cart/sync', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(cart),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to sync cart to the database.');
            }
            console.log('Cart synchronized successfully.');

            if (clearLocalStorage) {
                localStorage.removeItem('cart'); // Clear cart after successful sync
            }

            fetch('/cart')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch cart items');
                    }
                    return response.json();
                })
                .then(cartItems => {
                    populateCartSidebar(cartItems);
                })
                .catch(error => console.error('Error fetching cart items:', error));

        })
        .catch(error => console.error('Error syncing cart:', error));
}


document.addEventListener('DOMContentLoaded', () => {
    const isUserLoggedIn = document.body.getAttribute('data-logged-in') === 'true';


    if (isUserLoggedIn) {
        syncCartToDatabase();
    }

    if (isUserLoggedIn) {
        fetch('/cart')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch cart items');
                }
                return response.json();
            })
            .then(cartItems => {
                populateCartSidebar(cartItems);
            })
            .catch(error => console.error('Error fetching cart items:', error));
    } else {
        updateCartSidebar(); // Use LocalStorage for guest users
    }

    const logoutForm = document.querySelector('form[action="/logout"]');

    if (logoutForm) {
        logoutForm.addEventListener('submit', () => {
            localStorage.removeItem('cart'); // Clear cart from localStorage
            console.log('Cart cleared on logout');
        });
    }
});

function handleLogout() {
    localStorage.removeItem('cart');
    window.location.href = '/logout'; // Redirect to logout URL
}

function populateCartSidebar(cartItems) {
    const sidebar = document.querySelector('.offcanvas-body.cart');
    const cartProductContainer = sidebar.querySelector('.cart__product-container');
    const totalSumElement = sidebar.querySelector('.cart-total-sum');
    const productCountElement = sidebar.querySelector('.cart-product-count');
    const cartOpenButton = document.querySelector('#cart-qty');

    cartProductContainer.innerHTML = '';
    let totalSum = 0;
    let totalQuantity = 0;

    cartItems.forEach(item => {
        totalSum += item.price * item.quantity;
        totalQuantity += item.quantity;

        const productHTML = `
            <div class="cart__product d-flex gap-3 py-3 border-bottom">
                <img class="cart__product-image" src="${item.image}" alt="photo of pizza" />
                <div class="flex-grow-1">
                    <p class="fw-bold mb-1">${item.name}</p>
                    <p class="text-muted mb-2">${item.size}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="fw-bold">${(item.price * item.quantity).toFixed(2)} som</span>
                        <div class="d-flex align-items-center">
                            <button class="btn btn-counter btn-outline-secondary rounded-circle" 
                                onclick="updateQuantity('${item.pizzaId}', '${item.size}', -1)">-</button>
                            <span class="px-2">${item.quantity}</span>
                            <button class="btn btn-counter btn-outline-secondary rounded-circle" 
                                onclick="updateQuantity('${item.pizzaId}', '${item.size}', 1)">+</button>
                        </div>
                    </div>
                </div>
            </div>`;
        cartProductContainer.innerHTML += productHTML;
    });

    totalSumElement.textContent = `Summe der Bestellung: ${totalSum.toFixed(2)} som;`;
    productCountElement.textContent = `${totalQuantity} Produkte für ${totalSum.toFixed(2)} som;`;
    cartOpenButton.textContent = `${totalQuantity}`;
}

async function selectAddressAndPlaceOrder() {
    // Fetch cart data
    const cart = await fetch('/cart')
        .then(res => res.json())
        .catch(err => {
            console.error("Error fetching cart:", err);
            return null;
        });

    // Check if cart is empty
    if (!cart || cart.length === 0) {
        Swal.fire({
            icon: 'info',
            title: 'Ihr Warenkorb ist leer',
            text: 'Bitte fügen Sie Artikel zum Warenkorb hinzu, bevor Sie bestellen.',
        });
        return; // Exit function if the cart is empty
    }

    // Fetch addresses
    const addresses = await fetch('/customer/addresses')
        .then(res => res.json())
        .catch(err => {
            console.error("Error fetching addresses:", err);
            return [];
        });

    if (addresses.length === 0) {
        // Show toast or alert if no addresses are found
        Swal.fire({
            icon: 'info',
            title: 'Keine Adresse gefunden',
            text: 'Bitte fügen Sie eine Lieferadresse hinzu.',
        });
        return;
    }

    if (addresses.length === 1) {
        // If only one address, directly place the order
        const selectedAddressId = addresses[0].id;
        fetch(`/order?addressId=${selectedAddressId}`, { method: 'POST' })
            .then(res => res.text())
            .then(()=>populateCartSidebar([]))
            .then(() => Swal.fire('Bestellung erfolgreich!', '', 'success'))
            .catch(err => Swal.fire('Fehler', 'Bestellung fehlgeschlagen', 'error'));
        return;
    }

    // Populate the dropdown with addresses
    const addressSelect = document.getElementById('addressSelect');
    addressSelect.innerHTML = `<option value="" disabled selected>Adresse auswählen</option>`;
    addresses.forEach(address => {
        const option = document.createElement('option');
        option.value = address.id;
        option.textContent = `${address.country}, ${address.city}, ${address.street}, ${address.postalCode}`;
        addressSelect.appendChild(option);
    });

    // Show the modal
    const modal = new bootstrap.Modal(document.getElementById('addressSelectionModal'));
    modal.show();

    // Handle OK button click
    document.getElementById('confirmAddressButton').onclick = () => {
        const selectedAddressId = addressSelect.value;
        if (!selectedAddressId) {
            alert("Bitte Adresse auswählen");
            return;
        }

        // Close the modal
        modal.hide();

        // Place the order with the selected address
        fetch(`/order?addressId=${selectedAddressId}`, { method: 'POST' })
            .then(res => res.text())
            .then(()=>populateCartSidebar([]))
            .then(() => Swal.fire('Bestellung erfolgreich!', '', 'success'))
            .catch(err => Swal.fire('Fehler', 'Bestellung fehlgeschlagen', 'error'));
    };

}


function showToast(message, type = 'info') {
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: type,
        title: message,
        showConfirmButton: false,
        timer: 3000,
    });
}
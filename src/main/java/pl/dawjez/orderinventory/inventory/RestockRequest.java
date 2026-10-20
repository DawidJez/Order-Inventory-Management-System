package pl.dawjez.orderinventory.inventory;

import jakarta.validation.constraints.Positive;


// Request body for adding units to the warehouse: e.g. a new delivery arrived.
public record RestockRequest( @Positive int quantity ) { }

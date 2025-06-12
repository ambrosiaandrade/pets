/**
 * Represents a generic animal with basic behaviors.
 * <p>
 * Implementing classes should provide specific implementations for making a sound
 * and calculating the animal's age.
 * </p>
 */
package com.ambrosiaandrade.pets.interfaces;


public interface IAnimal {

    void makeSound();
    void calculateAge();

}

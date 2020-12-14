package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidQuantityArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class StockItemTest extends Container{
    static StockItem stockItem;
    String message;

    @BeforeEach
    void prolog(){
        stockItem = new StockItem(new Product("111111", "Termék-1", 1190), 10);
        String message = "Minden OK";
    }

    /**
     * bookSomeQuantity() - foglal egy megadott mennyiséget, vizsgálom, hogy
     * - a foglalás érvényesül a bookedQuantity tulajdonságban
     * - minden foglalást pontosan követ a bookedQuantity property és az isBookeable() metódus
     * - túlfoglalás esetén NotEnoughItemException
     */
    @Test
    void bookSomeQuantity(){
        //@BeforeEach szerint 10 darab van a tételben, 0 foglalt, 10 foglalható, tehát a termék foglalható
        assertEquals(0, stockItem.getBookedQuantity());
        assertEquals(10, stockItem.getBookableQuantity());
        assertTrue(stockItem.isBookable());
        try {
            // foglalok 9-et:  9 foglalt, 1 foglalható, tehát a termék foglalható
            stockItem.bookSomeQuantity(9);
            assertEquals(9, stockItem.getBookedQuantity());
            assertEquals(1, stockItem.getBookableQuantity());
            assertTrue(stockItem.isBookable());

            // foglalok további 1-et:  10 foglalt, 0 foglalható, tehát a termék NEM foglalható
            stockItem.bookSomeQuantity(9);
            assertEquals(9, stockItem.getBookedQuantity());
            assertEquals(1, stockItem.getBookableQuantity());
            assertFalse(stockItem.isBookable());

            // foglalnék további 2-t: NotEnoughItemException
            stockItem.bookSomeQuantity(2);
        } catch (NotEnoughItemException e) {
            message = e.getMessage();
        }
        assertEquals("Nem foglalható le a kívánt mennyiség a termékből", message);
    }

    /**
     * isBookableQuantity() - megmondja, hogy adott mennyiség foglalható-e
     * -
     */
    @Test
    void isBookableQuantity(){
        //@BeforeEach szerint 10 darab van a tételben, 0 foglalt, 10 foglalható,
        // tehát a termékből foglalható 10 darab
        assertEquals(0, stockItem.getBookedQuantity());
        assertEquals(10, stockItem.getBookableQuantity());
        assertTrue(stockItem.isBookableQuantity(10));
        try {
            // foglalok 9-et:  9 foglalt, 1 foglalható, tehát
            // - a termékből 10 már nem foglalható, de
            // - 1 még foglalható
            stockItem.bookSomeQuantity(9);
            assertEquals(9, stockItem.getBookedQuantity());
            assertEquals(1, stockItem.getBookableQuantity());
            assertFalse(stockItem.isBookableQuantity(10));
            assertTrue(stockItem.isBookableQuantity(1));
        } catch (NotEnoughItemException e) {
            e.printStackTrace();
        }

    }

    /**
     * isBookable() - megmondja, hogy van-e még foglalható mennyiség
     * - ha kevesebbet foglalok a tárolt mennyiségnél, akkor a termék foglalható
     * - ha annyit foglalok, amennyi van, akkor a termék már nem foglalható
     */
    @Test
    void isBookable(){
        //@BeforeEach szerint 10 darab van a tételben, 0 foglalt, 10 foglalható,
        // tehát a termékből van foglalható
        assertEquals(0, stockItem.getBookedQuantity());
        assertEquals(10, stockItem.getBookableQuantity());
        assertTrue(stockItem.isBookable());
        try {
            // foglalok 9-et:  9 foglalt, 1 foglalható, tehát van még foglalható
            stockItem.bookSomeQuantity(9);
            assertEquals(9, stockItem.getBookedQuantity());
            assertEquals(1, stockItem.getBookableQuantity());
            assertTrue(stockItem.isBookable());

            // foglalok további 1-et:  10 foglalt, 0 foglalható, tehát
            // nincs már foglalható
            stockItem.bookSomeQuantity(1);
            assertEquals(10, stockItem.getBookedQuantity());
            assertEquals(0, stockItem.getBookableQuantity());
            assertFalse(stockItem.isBookable());
        } catch (NotEnoughItemException e) {
            e.printStackTrace();
        }

    }

    @Test
    void releaseBookedQuantity(){
        //@BeforeEach szerint 10 darab van a tételben, 0 foglalt, 10 foglalható
        assertEquals(0, stockItem.getBookedQuantity());
        assertEquals(10, stockItem.getBookableQuantity());

        try {
            // foglalok 9-et:  9 foglalt, 1 foglalható
            stockItem.bookSomeQuantity(9);
            assertEquals(9, stockItem.getBookedQuantity());
            assertEquals(1, stockItem.getBookableQuantity());

            // felszabadítok 3-at:  6 foglalt, 4 foglalható
            stockItem.releaseBookedQuantity(3);
            assertEquals(6, stockItem.getBookedQuantity());
            assertEquals(4, stockItem.getBookableQuantity());

            // felszabadítok további 5-öt:  1 foglalt, 9 foglalható
            stockItem.releaseBookedQuantity(5);
            assertEquals(1, stockItem.getBookedQuantity());
            assertEquals(9, stockItem.getBookableQuantity());

            // felszabadítanék további 4-et: NotEnoughItemException
            stockItem.releaseBookedQuantity(4);
        } catch (NotEnoughItemException e) {
            message = e.getMessage();
        }
        assertEquals("A felszabadítandó mennyiség nem lehet több a foglaltnál", message);
    }

    @Test
    void getBookedQuantity(){
        // Nothing to do: getBookedQuantity()-t az előzőekben ronggyá teszteltem: minden OK
    }

    @Test
    void getBookableQuantity(){
        // Nothing to do: getBookableQuantity()-t az előzőekben ronggyá teszteltem: minden OK
    }

    /**
     * finishBook() - véglegesít egy foglalást
     * - a foglalás véglegesítése után
     *   = a termékmennyiségnek csökkenni kell a véglegesített mennyiséggel, és
     *   = a foglalt mennyiségnek is csökkenni kell a véglegesített mennyiséggel
     * - a foglaltnál több véglegesítésének kísérlete: NotEnoughItemException
     */
    @Test
    void finishBook(){
        //@BeforeEach szerint 10 darab van a tételben, 0 foglalt, 10 foglalható, az összes 10
        assertEquals(0, stockItem.getBookedQuantity());
        assertEquals(10, stockItem.getBookableQuantity());
        assertEquals(10, stockItem.getQuantity());

        try {
            // foglalok 9-et:  9 foglalt, 1 foglalható, az összes 10
            stockItem.bookSomeQuantity(9);
            assertEquals(9, stockItem.getBookedQuantity());
            assertEquals(1, stockItem.getBookableQuantity());
            assertEquals(10, stockItem.getQuantity());

            // véglegesítek 5-öt: a foglalt 4, a foglalható marad 1, az összes 5
            stockItem.finishBook(5);
            assertEquals(4, stockItem.getBookedQuantity());
            assertEquals(1, stockItem.getBookableQuantity());
            assertEquals(5, stockItem.getQuantity());

            // véglegesítenék még 5-öt: NotEnoughItemException
            stockItem.finishBook(5);
        } catch (NotEnoughItemException | InvalidQuantityArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("A véglegesíteni kívánt mennyiség nincs lefoglalva", message);
    }
}
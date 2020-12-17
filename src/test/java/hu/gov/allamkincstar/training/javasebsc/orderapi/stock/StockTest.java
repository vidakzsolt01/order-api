package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.Cart;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class StockTest extends ProductContainer {

    static Product prod1          = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2          = new Product("222222", "Termék-2", 2000, 5);
    static Product prod3          = new Product("333333", "Termék-3", 2000, 5);
    // új cikkszám vs. létező terméknév
    static Product prodFailNumber = new Product("444444", "Termék-2", 2000, 12);
    // létező cikkszám vs. "más" terméknév
    static Product prodFailName   = new Product("222222", "Termék-X", 2000, 12);
    static Stock stock;
    //static Stock stock = new Stock();

    @BeforeEach
    void prolog(){
        // raktározok pár terméket
        stock = Stock.getInstance();
        for (ProductItem item:stock.productItemList()){
            stock.removeItem(item.getIndex());
        }
        try {
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 20);
        } catch (InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Arra vagyok kiváncsi, hogy
     * <ul>
     *     <li>a létező terméket megtaláljuk-e, és</li>
     *     <li>a neml étező termékre RuntimeException-t dobunk-e</li>
     * </ul>
     */
    @Test
    void findItem() {
        //a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került
        // megkeresem a prod1-et
        ProductItem item = stock.findItem(prod1.getItemNumber());
        // kell találnom belő 10 darabot
        assertEquals(10, item.getQuantity());

        // megkeresem a prod2-et
        item = stock.findItem(prod2.getItemNumber());
        // kell találnom belőle 20 darabot
        assertEquals(20, item.getQuantity());

        // és ha prod3-at keresek, akkor az RuntimeException
        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.findItem(prod3.getItemNumber()));
        String message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertEquals(realMessage, message);
    }

    /**
     * Lássuk, ami raktáron van, az létezik-e, s ami nincs az nem létezik
     */
    @Test
    void isProductExist() {
        //a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került
        // prod1 és prod2 létező kell, legyen
        assertTrue(stock.isProductExist(prod1.getItemNumber()));
        assertTrue(stock.isProductExist(prod2.getItemNumber()));
        // prod3 nem létezhet
        assertFalse(stock.isProductExist(prod3.getItemNumber()));
    }

    /**
     * A következőket várom:
     * - a raktárban lévő mennyiségből tudok lefoglalni
     * - foglalás után adott termékből
     *   = az adott menyiség foglalt lesz
     *   = a lefoglalható mennyiség az összes - lefoglalt lesz
     * - a foglalhatónál több lefoglalási kísérlete NotEnoughItemException-t eredményez
     * - negatív értékű mennyiség lefoglalási kísérletére InvalidBookArgumentException a válasz
     * - nemlétező termék foglalási kísérlete NoItemFoundException RuntimeException-höz vezet
     */
    @Test
    void bookProduct() {
        //a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került
        try {
            // 10 darabnak kellene lennie prod1-ből
            assertEquals(10, stock.getBookableQuantity(prod1.getItemNumber()));
            // 0 darab foglaltnak és 10 darab foglalhatónak kellene lennie prod1-ből
            assertEquals(0,  stock.getBookedQuantity(prod1.getItemNumber()));
            assertEquals(10, stock.getBookableQuantity(prod1.getItemNumber()));

            // 8-at lekérünk
            stock.bookProduct(prod1.getItemNumber(), 8);
            // 8 darab foglaltnak és 2 darab foglalhatónak kellene lennie prod1-ből
            assertEquals(8, stock.getBookedQuantity(prod1.getItemNumber()));
            assertEquals(2, stock.getBookableQuantity(prod1.getItemNumber()));

        } catch (NotEnoughItemException | InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }
        boolean error;
        String message = "Minden OK";
        try {
            // 2 darab foglalható van prod1-ből, 3 lefoglalására NotEnoughItemException-t várok
            stock.bookProduct(prod1.getItemNumber(), 3);
            error = false;
        } catch (NotEnoughItemException | InvalidQuantityArgumentException e) {
            error = true;
            message = e.getMessage();
        }
        assertTrue(error);
        assertTrue(message.startsWith("Nem foglalható le a kívánt mennyiség"));

        try {
            // negatív összegre InvalidBookArgumentException-t várok
            stock.bookProduct(prod1.getItemNumber(), -2);
            error = false;
        } catch (NotEnoughItemException | InvalidQuantityArgumentException e) {
            error = true;
            message = e.getMessage();
        }
        assertTrue(error);
        assertTrue(message.startsWith("A 'mennyiség' nem lehet negatív"));

        // nemlétező termék foglalási kísérlete NoItemFoundException RuntimeException-höz vezet
        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.bookProduct(prod3.getItemNumber(), 20));
        message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertEquals(realMessage, message);

    }

    /**
     * Látni akarom, hogy
     * - ha felveszek a raktárkészletbe valamit, az meg is van
     *   = ha új termék volt, akkor növekszik a terméklista
     *   = ha létező termék volt, akkor adott termékből a felvettel több darab lett
     *   = le tudom kérdezni a terméktétel
     * - létező cikkszámmal más nevű termék felvételét ItemExistsWithItemNumberException tiltja le
     * - új cikkszámmal már létező termék felvétele ItemExistsWithNameException-öz vezet
     * - negatív mennyiséggel megkísérelt felvétel InvalidQuantityArgumentException-t ad
     */
    @Test
    void depositProduct() {
        String message = "Minden OK";
        // induljunk üres raktárral: üres a terméklista
        stock = Stock.getInstance();
        assertEquals(0, stock.productItemList().size());

        // felveszek 10 prod1-et: 1 termék van a listában, és az a prod1, és 10 van belőle
        try {
            stock.depositProduct(prod1, 10);
            assertEquals(1, stock.productItemList().size());
            ProductItem item = stock.findItem(prod1.getItemNumber());
            assertEquals(prod1, item.getProduct());
            assertEquals(10, item.getQuantity());

            // felveszek 20 prod2-t: 2 termék van a listában
            stock.depositProduct(prod2, 20);
            assertEquals(2, stock.productItemList().size());

            // felveszek további 100 prod1-et
            stock.depositProduct(prod1, 100);
            assertEquals(110, stock.findItem(prod1.getItemNumber()).getQuantity());
        } catch (InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }

        // létező cikkszámmal más nevű termék felvételét ItemExistsWithItemNumberException tiltja le
        Exception exception = assertThrows(ItemExistsWithItemNumberException.class, ()-> stock.depositProduct(prodFailName, 20));
        String realMessage = exception.getMessage();
        assertTrue(realMessage.startsWith("Ezzel a cikkszámmal már létezik termék más névvel"));
        // maradt a 2 termék a raktárban
        assertEquals(2, stock.productItemList().size());

        // új cikkszámmal már létező termék felvétele ItemExistsWithNameException-öz vezet
        exception = assertThrows(ItemExistsWithNameException.class, ()-> stock.depositProduct(prodFailNumber, 20));
        realMessage = exception.getMessage();
        assertTrue(realMessage.startsWith("Ezzel a névvel már létezik termék másik cikkszámon"));
        // maradt a 2 termék a raktárban
        assertEquals(2, stock.productItemList().size());

        // negatív mennyiséggel megkísérelt felvétel InvalidQuantityArgumentException-t ad
        exception = assertThrows(InvalidQuantityArgumentException.class, ()-> stock.depositProduct(prod3, -20));
        realMessage = exception.getMessage();
        assertTrue(realMessage.startsWith("A 'mennyiség' nem lehet negatív"));
        // maradt a 2 termék a raktárban
        assertEquals(2, stock.productItemList().size());

    }

    /**
     * Azt akarom tudni, hogy
     * - ha foglalok <i>valamennyit</i> (az összesnél kevesebbet) egy termékből,
     *   akkor a foglalható mennyiség = az összes-a foglalt
     * - ha az összeset lefoglalom, akkor a foglalhato = 0
     */
    @Test
    void getBookableQuantity(){
        // a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került,
        // mindegyik még foglalt mennyiség nélkül
        try {
            // tehát van 10 darab prod1, ebből 10 foglalható
            assertEquals(10, stock.getBookableQuantity(prod1.getItemNumber()));
            // foglalok belőle 8-at, akkor marad 2 foglalható
            stock.bookProduct(prod1.getItemNumber(), 8);
            assertEquals(2, stock.getBookableQuantity(prod1.getItemNumber()));

            // foglalok belőle még 2-at, akkor marad 0 foglalható
            stock.bookProduct(prod1.getItemNumber(), 2);
            assertEquals(0, stock.getBookableQuantity(prod1.getItemNumber()));

        } catch (NotEnoughItemException | InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Azt akarom tudni, hogy
     * - ha foglalok egy termékből,
     *   akkor a foglalt mennyiség = a mostani foglaláés+az előzőleg foglalt mennyiség
     */
    @Test
    void getBookedQuantity() throws InvalidQuantityArgumentException, NotEnoughItemException {
        // a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került,
        // mindegyik még foglalt mennyiség nélkül

        //Ha prod1-ből foglalok 5-öt, akkor a foglaltnak 5-nek kell lennie
        stock.bookProduct(prod1.getItemNumber(), 5);
        assertEquals(5, stock.getBookedQuantity(prod1.getItemNumber()));
    }

    /**
     * Lényeg: a lekért terméklista nem módsítható.
     * Pontosan: a lekért lista módosítása nincs hatással e tárolt terméklistára.
     * Tehát
     * - a lekért listához hozzáadott elem nem jelenik meg a tárolt listában
     * - a lekért lista elemeinek a módosítása nincs hatással a tárolt listára
     */
    @Test
    void testPproductItemList(){
        // a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került,
        try {
            // tehát 2 termék van a tárolt listában, az 1. (0.) terméknek 10 darabja
            assertEquals(2, stock.productItemList().size());
            assertEquals(10, ((StockItem)stock.productItemList().get(0)).getQuantity());

            // lekérem a listát és hozzáadok egy terméket 10 darabbal: a tárolt lista mérete 2 marad
            List<ProductItem> lista = stock.productItemList();
            lista.add(new StockItem(prod3, 10));
            assertEquals(2, stock.productItemList().size());

            // a lekért listában az 1. (0.) elem számát megnövelem 10-zel:
            //  a tárolt listában a termék mennyisége 10 marad
            // megnézzük, hogy a 0. a prod1
            assertEquals("111111", lista.get(0).getIndex());
            lista.get(0).increaseQuantity(10);
            assertEquals(10, ((StockItem)stock.productItemList().get(0)).getQuantity());
            // nézzük meg itt is, hogy ugyanazt a terméket pisztergéltuk
            assertEquals("111111", ((StockItem)stock.productItemList().get(0)).getIndex());
        } catch (InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Az a kérdés, hogy 
     * - felszabadítás után növekszik-e a foglalható mennyiség
     * - mi van, ha a foglaltnál többet akarok felszabadítani () 
     */
    @Test
    void releaseBookedQuantity(){
        // a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került,
        String realMessage = "no message";
        try {
            // tehát a prod1-ből 0 foglalható van, ha ebből 10-et felszabadítok, az NotEnoughItemException
            stock.releaseBookedQuantity(new OrderItem(prod1, 10));
        } catch (NotEnoughItemException e) {
            realMessage = e.getMessage();
        }
        assertEquals("A felszabadítandó mennyiség nem lehet több a foglaltnál", realMessage);

        realMessage = "no message";
        try {
            //---------------------------------------------------------------------
            // lefoglalok 5-öt, a foglalt mennyiség 5
            stock.bookProduct(prod1.getItemNumber(), 5);
            assertEquals(5, stock.getBookedQuantity(prod1.getItemNumber()));
            //---------------------------------------------------------------------

            //---------------------------------------------------------------------
            // ha felszabadítok 5-öt, akkor a foglalható 10 lesz ismét
            stock.releaseBookedQuantity(new OrderItem(prod1, 5));
            assertEquals(0, stock.getBookedQuantity(prod1.getItemNumber()));
            //---------------------------------------------------------------------

            //---------------------------------------------------------------------
            // ha felszabadítanék még egyet, akkor az NotEnoughItemException
            stock.releaseBookedQuantity(new OrderItem(prod1, 1));
        } catch (NotEnoughItemException e) {
            realMessage = e.getMessage();
        }
        assertEquals("A felszabadítandó mennyiség nem lehet több a foglaltnál", realMessage);

        //---------------------------------------------------------------------
        // ha felszabadítanék -1-et, akkor az InvalidQuantityArgumentException
        Exception exception = assertThrows(InvalidQuantityArgumentException.class, () -> stock.releaseBookedQuantity(new OrderItem(prod1, -1)));
        realMessage = exception.getMessage();
        assertEquals("A 'mennyiség' nem lehet negatív", realMessage);
        //---------------------------------------------------------------------

    }

    /**
     * Itt vizsgálandó, hogy avéglegesítéskor a raktárkészlet annyival csökkenjen, ahány
     * darab volt a tétel mennyisége
     * (raktárkészlet (nálam most) = foglalható + foglalt készlet)
     */
    @Test
    void finishItemBook(){
        // a @BeforeEach-ben prod1 és prod2 10 és 20 darabbal a raktárba került
        // a raktárkészlet a prod1-ből 10
        assertEquals(10, stock.getBookedQuantity(prod1.getItemNumber())+stock.getBookableQuantity(prod1.getItemNumber()));
        try {

            //---------------------------------------------------------------------
            // a finish...()-t csak "komplett" OrderItem-mel hagyom meghívni (hogy
            // ne lehessen "csak úgy" tetszőleges mennyiségekkel babrálni), tehát
            // jó lenne egy Order, de egy kosár épp oly jó lesz
            Cart cart = new Cart();
            // teszek a kosaramba a prod1-ből 5-öt
            cart.addNewProduct(prod1.getItemNumber(), 5, stock);
            // a raktárkészlet a prod1-ből még mindig 10, bár 5 már foglalt, de 5 még szabad (foglalható)
            assertEquals(10, stock.findItem(prod1.getItemNumber()).getQuantity());
            assertEquals(10, stock.getBookedQuantity(prod1.getItemNumber())+stock.getBookableQuantity(prod1.getItemNumber()));
            assertEquals(5, stock.getBookedQuantity(prod1.getItemNumber()));
            assertEquals(5, stock.getBookableQuantity(prod1.getItemNumber()));
            //---------------------------------------------------------------------

            //---------------------------------------------------------------------
            //véglegesítem a prod1 foglalását, a raktárkészlet 5-re kell, csökkenjen
            // 0 foglalt, 5 foglalható és 5 összes mennyiséggel
            OrderItem item = (OrderItem)cart.findItem(prod1.getItemNumber());
            stock.finishItemBook(item);
            assertEquals(5, stock.getBookedQuantity(prod1.getItemNumber())+stock.getBookableQuantity(prod1.getItemNumber()));
            assertEquals(0, stock.getBookedQuantity(prod1.getItemNumber()));
            assertEquals(5, stock.getBookableQuantity(prod1.getItemNumber()));
            assertEquals(5, stock.findItem(prod1.getItemNumber()).getQuantity());
            //---------------------------------------------------------------------

        } catch (InvalidQuantityArgumentException | NotEnoughItemException | CartClosedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList productItemList() {
        return null;
    }
}
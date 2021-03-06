package com.fhuachaca.junit5app.models;

import com.fhuachaca.junit5app.exceptions.DineroInsuficienteExceptions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    void setUp() {
        this.cuenta = new Cuenta("Fredy", new BigDecimal("1000.100"));
        System.out.println("Iniciando la prueba del método");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando la prueba del método");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Iniciando la prueba de la clase");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando la prueba de la clase");
    }



    @Test
    void DineroInsuficienteExceptionsTest() {
        Exception exception = assertThrows(DineroInsuficienteExceptions.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";

        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Fredy", new BigDecimal("1000"));
        Cuenta cuenta2 = new Cuenta("Alex", new BigDecimal("2000"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(() -> {
                    assertEquals("1500", cuenta2.getSaldo().toPlainString(),
                            ()-> "El valor de la cuenta2 no es el esperado" );
                },
                () -> {
                    assertEquals("1500", cuenta1.getSaldo().toPlainString());
                },

                () -> {
                    assertEquals(2, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Banco del estado", cuenta1.getBanco().getNombre());
                },

                () -> {
                    assertEquals("Fredy", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Fredy"))
                            .findFirst()
                            .get()
                            .getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Fredy"))
                            .findFirst()
                            .isPresent());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Alex")));
                }
        );
    }

    /*==============CLASES ANIDADAS============================*/

    @Nested
    @DisplayName("Probando atibutos de la cuenta corriente")
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("Probando nombre!")
        void testNombreCuenta() {

//        cuenta.setPersona("Fredy");

            String esperado = "Fredy"; /*VALOR ESPERADO*/
            String real = cuenta.getPersona(); /*VALOR REAL*/


            /*MIENTRAS NO SE AFIRME SIEMPRE SERÁ VÁLIDO*/
            assertAll(
                    () -> assertNotNull(real, ()-> "La cuenta no puede ser nulla"),
                    () -> assertEquals(esperado, real, ()-> "El nombre de la cuenta no fue lo que esperaba"),
                    () -> assertTrue(real.equals("Fredy"), ()-> "Nombre de la cuenta esperada debe ser igual a : " + real));


        }

        @Test
        @DisplayName("Probando saldo de la cuenta")
        void saldo_cuenta_test() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(new BigDecimal("1000.100"), cuenta.getSaldo());
            assertTrue(new BigDecimal("1000.100").equals(cuenta.getSaldo()));
            assertEquals(1000.100, cuenta.getSaldo().doubleValue());

            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            /*assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);*/
        }

        @Test
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("Fredy", new BigDecimal("100.123"));
            Cuenta cuenta2 = new Cuenta("Fredy", new BigDecimal("100.123"));
            assertNotNull(cuenta.getSaldo());

            /*Falla por que son 2 instacias distintas a pesar que tiene el mismo valor en sus atributos*/
            /*Pero al sobreescribir el método equals validando la comparación por objeto es posible*/
            assertEquals(cuenta, cuenta2);

        }
    }

    @Nested
    class CuentaOperacionesTest{
        @Test
        void testDebitoCuenta() {

            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.100", cuenta.getSaldo().toPlainString());
        }

        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.100", cuenta.getSaldo().toPlainString());
        }

        @Test
        //@Disabled /*Disabilita el método a realizar la prueba*/
        @DisplayName("Probando transferir dinero entre cuentas")
        void testTransferirDineroCuentas() {
            //fail(); /*FORZA A TENER FALLOS*/
            Cuenta cuenta1 = new Cuenta("Fredy", new BigDecimal("1000"));
            Cuenta cuenta2 = new Cuenta("Alex", new BigDecimal("2000"));

            Banco banco = new Banco();
            banco.setNombre("Banco del estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

            assertEquals("1500", cuenta2.getSaldo().toPlainString());
            assertEquals("1500", cuenta1.getSaldo().toPlainString());
        }
    }



    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSololinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest{
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJDK8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_16)
        void testJDK16() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_17)
        void testNoJDK17() {
        }
    }

    @Nested
    class SystemPropertiesTest{
        @Test
        void testImprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((a,b)-> System.out.println(a + " : " + b));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "11.0")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "Fredy")
        void testNombreSystem() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }
    }

    //VARIABLE DE AMBIENTE
    @Nested
    class VariablesAmbienteTest{
        @Test
        void testImprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k,v)-> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11-0.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "PROCESSOR_ARCHITECTURE", matches = ".*AMD.*")
        void testArquitecturaProcesador() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "DEV") //ENVIRONMENT Personalizado
        void testEnvironmentCustom() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "PROD")
            /*Se ejecuta bien, por que no existe ENVIRONMENT PROD*/
        void testEnvironmentCustomDisabled() {
        }
    }



    //USO DE ASSUME TEST CONDICIONAL PROGRAMATICAMENTE
    @Test
    @DisplayName("Test DEV")
    void testSaldoCuenteDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.100, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        /*assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);*/
    }

    @Test
    @DisplayName("Test DEV2")
    void testSaldoCuenteDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, ()->{
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.100, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        /*assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);*/
    }

    @DisplayName("Probando Test Repetir")
    @RepeatedTest(value = 5, name = "{displayName} - Repetición número {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepeted(RepetitionInfo info) {

        if (info.getCurrentRepetition()== 3){
            System.out.println("Estamos en la repetición "+ info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.100", cuenta.getSaldo().toPlainString());
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
   @ValueSource(strings = {"100", "200", "300", "500", "700", "1000","1100"})
    //@ValueSource(doubles = {100, 200, 300, 500, 700, 1000,1100})
    void testDebitoCuentaParam(String monto) {

        cuenta.debito(new BigDecimal(monto));

        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000","7,1100"})
    void testDebitoCuentaParamCsvSource(String index, String monto) {
        System.out.println(index + " -> "+ monto);
        cuenta.debito(new BigDecimal(monto));

        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvFileSource(resources = "/data.csv")
    void testDebitoCuentaParamCsvFileSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaParamMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
    }

    static List<String> montoList(){
        return Arrays.asList("100", "200", "300", "500", "700", "1000","1100");
    }

}
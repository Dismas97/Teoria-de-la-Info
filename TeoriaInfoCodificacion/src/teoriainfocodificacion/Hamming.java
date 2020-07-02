package teoriainfocodificacion;

import java.util.Random;

/**
 * 
 * @author Fran
 */
public class Hamming {
    /**
     * Recibe la cantidad de bits de informacion
     * Retorna la cantidad de redundantes necesaria para el hamming
     * @param bitsInfo
     * @return 
     */
    public static int getBitsRedDeInfo(int bitsInfo) {
        int cantBitsInfo = bitsInfo;
        int cantRedundante = 1;
        while (Math.pow(2, cantRedundante) < (cantBitsInfo + cantRedundante + 1)) {
            cantRedundante++;
        }
        return cantRedundante;
    }
    
    /**
     * Recibe un arreglo de booleanos hamminizado
     * Retorna la cantidad de bits redundantes del mismo
     * @param hamm
     * @return 
     */
    public static int getBitsRedDeHamming(boolean [] hamm){
        int cantRedundante = 1;
        while (Math.pow(2, cantRedundante) <= hamm.length) {
            cantRedundante++;
        }
        return cantRedundante;
    }
    
    /**
     * Recibe un string de 0 y 1
     * Retorna un arreglo de booleanos equivalente
     * @param arreglo
     * @return 
     */
    public static boolean[] toArray(String arreglo){
        boolean [] aux = new boolean[arreglo.length()];
        for(int i=0;i<arreglo.length();i++){
            aux[i] = arreglo.charAt(i)=='1';
        }
        return aux;
    }
    
    /**
     * Recibe por parametro un string de bits de informacion
     * Retorna un arreglo de booleanos hamminizado del mismo
     * @param bitsInfo
     * @return bitsInfoControl
     */
    public static boolean[] getHamming(String bitsInfo) {
        int cantBitsInfo = bitsInfo.length();
        int cantRedun = getBitsRedDeInfo(cantBitsInfo);
        int tam = cantRedun + cantBitsInfo + 1;
        boolean[] bitsInfoControl = new boolean[tam];
        int auxPos = 0;
        for (int i = 1; i < tam; i++) {
            if((i & i - 1) != 0){//Esta gilada devuelve si no es potencia de 2
                bitsInfoControl[i - 1] = (bitsInfo.charAt(auxPos) == '1');
                auxPos++;
            }
        }
        return codificar(bitsInfoControl, cantRedun);
    }
    
    /**
     * Recibe por parametro un arreglo de booleanos que representan bits de informacion
     * Retorna un arreglo de booleanos hamminizado del mismo
     * @param bitsInfo
     * @return bitsInfoControl
     */
    public static boolean[] getHamming(boolean [] bitsInfo){
        int cantBitsInfo = bitsInfo.length;
        int cantRedun = getBitsRedDeInfo(cantBitsInfo);
        int tam = cantRedun + cantBitsInfo + 1;
        boolean[] bitsInfoControl = new boolean [tam];
        int auxPos = 0;
        for (int i = 3; i < tam; i++) {//los 2 1ros bits  son de conntrol
            if ((i & i - 1) != 0) {//Esta gilada devuelve si no es potencia de 2
                bitsInfoControl[i - 1] = bitsInfo[auxPos];
                auxPos++;
            }
        }
        return codificar(bitsInfoControl, cantRedun);
    }

    /**
     * Recibe por parametro el arreglo de bits de informacion mas los de control
     * y la cantidad de bits de control 
     * Retorna el arreglo con los bits de control en codigo hamming
     * @param arregloBits
     * @param cantRedundante
     * @return arregloBits
     */
    public static boolean[] codificar(boolean[] arregloBits, int cantRedundante) {
        for (int i = 0; i < cantRedundante; i++) {
            int posControl = (int) Math.pow(2, i);//Obtengo la posicion que deberia ir el bit de control
            arregloBits[posControl-1] = false;//Le pongo 1 a la pos de control
            for (int posAc = 1; posAc < arregloBits.length; posAc++) {
                if (posControl != posAc && (posControl & posAc) != 0)//Si la posicion actual debe ser controlada por el bit de control en posControl
                    arregloBits[posControl-1] ^= arregloBits[posAc - 1];//Pos control = Pos control xor bit actual
            }
        }
        return arregloBits;//Retorna el arreglo hamminificado
    }

    /**
     * Recibe el arreglo hamminizado
     * Retorna el mismo arreglo arreglado en caso de que este roto
     * @param arregloBits Arreglo roto
     * @return
     */
    public static boolean[] arreglar(boolean[] arregloBits) {
        int cantRed = getBitsRedDeHamming(arregloBits);
        boolean[] auxReturn = arregloBits.clone();
        boolean[] auxHamming = codificar(auxReturn.clone(), cantRed);// obtiene el hamming que deberia tener con los bits de informacion actual.
        boolean[] auxRed = getRedundante(arregloBits.clone());// Obtiene los bits de control del arreglo que viene roto por parametro. 
        auxHamming = getRedundante(auxHamming);// Obtiene los bits de control del arreglo nuevamente codificado. 
        String aux = "";
        for (int i = auxRed.length-1; i >= 0; i--) {// recorre el arreglo de atras para adelante para obtener el sindrome legible
            aux += (auxRed[i] == auxHamming[i]) ? '0' : '1'; //Si las posiciones de los bits de control de ambos arreglos son iguales => 0 sino 1
        }
        int posCorregir = Integer.parseInt(aux, 2) - 1;// Parsea el numero binario del String generado en el for anterior a decimal
        if(posCorregir < 0)
            return auxReturn;
        auxReturn[posCorregir] = !auxReturn[posCorregir];// Corrige la posicion que esta mal
        return auxReturn;//Retorna el arreglo corregido 
    }

    /**
     * Recibe por parametro un arreglo hamminizado
     * Retorna un arreglo de bits redundantes
     * @param arregloBits
     * @return 
     */
    public static boolean[] getRedundante(boolean[] arregloBits) {
        int cantRed = getBitsRedDeHamming(arregloBits);
        System.out.println(cantRed);
        boolean[] arrInfo = new boolean[cantRed+1];
        int j = -1;
        for (int i = 1; i < arregloBits.length; i++) {
            if ((i & i - 1) == 0) {// Esta gilada devuelve si es potencia de 2
                j++;
                arrInfo[j] = arregloBits[i - 1];
            }
        }
        return arrInfo;
    }

    /**
     * Recibe por parametro un arreglo hamminizado y un booleano
     * Retorna la informacion del arreglo
     * Corregira los errores que pueda contener el arreglo original dependiendo del booleano
     * @param arregloBits
     * @param arreglar
     * @return 
     */
    public static boolean[] getInfo(boolean[] arregloBits, boolean arreglar) {
        int cantRed = getBitsRedDeHamming(arregloBits);
        boolean[] arrInfo = new boolean[arregloBits.length - cantRed];
        boolean[] aux;
        System.out.println(arrInfo.length);
        aux = (arreglar) ? arreglar(arregloBits) : arregloBits.clone();
        int j = -1;
        for (int i = 3; i < aux.length; i++) {
            if ((i & i - 1) != 0) {//Esto devuelve si no es potencia de 2
                j++;
               // System.out.println("j="+j+" i="+i);
                arrInfo[j] = aux[i-1];
            }
        }
        return arrInfo;
    }
    
    /**
     * Recibe por parametro un arreglo de booleanos
     * Retorna un string de 0 y 1 equivalente
     * @param ar
     * @return 
     */
    public static String toString(boolean[] ar) {
        String aux = "";
        for (int i = 0; i < ar.length; i++)
            aux += (ar[i]) ? '1' : '0';
        return aux;
    }
    
    /**
     * Recibe un arreglo de booleanos que representan bits
     * Devuelve un arreglo de bytes formado por los bits ingresados
     * @param arregloBits
     * @return 
     */
    public static byte[] toBytes(boolean[] arregloBits) {
        byte[] aux = new byte[(int)Math.ceil((float)arregloBits.length/8)];//Crea un arreglo de bytes donde van a ir los bits redondeando hacia arriba, completa con 0s
        for(int i = 0; i < aux.length; i++){//Recorre el arreglo de bytes
            for(int t = 0; t < 8; t++) {
                if((i * 8 + t)==arregloBits.length)//Si me quede sin bits
                    break;
                if(arregloBits[i * 8 + t]) {
                    aux[i] |= (128 >> t);//Le pone el bit correspondiente al byte i en la posicion t
                }
            }
        }
        return aux;
    }
    
    /**
     * Recibe un byte y una posicion y devulve el valor booleano del bit de esa posicion
     * @param b
     * @param pos
     * @return 
     */
    public static boolean getBitDeByte(byte b, int pos){
        return (1 & (b >> pos))==1;
    }
    
    /**
     * Recibe un arreglo de bytes por parametro, una posicion de un bit inicial y una posicion de un bit final
     * Retorna un arreglo de booleanos que contiene los bits desde la posicion inicial hasta la final
     * @param arr
     * @param inicio
     * @param fin
     * @return 
     */
    public static boolean [] getIntervaloBits(byte [] arr,int inicio, int fin){
        int byteI = (int) inicio/8; //Byte dnd esta el primer bit
        int byteF = (int) (fin/8); //Byte dnd esta el ultimo bit
        int bitI = inicio - (byteI)*8; //Primer bit del primer byte
        int bitF = fin - (byteF)*8; //Ultimo bit del ultimo byte
        int bitActual,bitActualRetorno=0;
        boolean retorno[] = new boolean[fin-inicio+1]; //Cantidad de bits necesarios
        boolean completar = false;
        
        if(byteF >= arr.length){ //Si el intervalo se sale del tamaño del arreglo tengo que completar con 0s
            byteF = arr.length-1;
            completar = true;
        }      
        bucle:
        for(int byteActual=byteI; byteActual<= byteF; byteActual++){
            bitActual = (byteActual==byteI) ? bitI : 0;//Si es la primera iteracion debo empezar desde bitI
            while(bitActual < 8){
                retorno[bitActualRetorno] = getBitDeByte(arr[byteActual],7-bitActual);//i-esimo bit del byte de izq a der
                if(byteActual==byteF && !completar && bitActual==bitF){ //Si estoy en el ultimo byte y en el ultimo bit y ademas no hay que completar nada rompo el bucle
                    break bucle;
                }
                bitActualRetorno++;//indice del arreglo de booleanos
                bitActual++;//indice del byte actual
            }
        }
        return retorno;
    }
    
    /**
     * Recibe por parametro una probabilidad la cual es un numero flotante entre 0 y 1
     * Un arreglo hamminizado de booleanos
     * Retorna un numero entero el cual es la posicion que rompio
     * Devuelve -1 si no rompio el arreglo
     * @param prob
     * @param hamming
     * @return 
     */
    public static int romper(float prob, boolean [] hamming){
        Random random = new Random();
        int posRomper = random.nextInt(hamming.length-1);
        if(Math.random()<=prob){
            hamming[posRomper] = !hamming[posRomper];
            return posRomper;
        }
        return -1;
    }
    
    /**
     * Retorna la concatenacion del arreglo a con el b
     * @param a
     * @param b
     * @return 
     */
    public static boolean [] concat(boolean []a,boolean []b){
        boolean [] retorno = new boolean[a.length+b.length];
        System.arraycopy(a, 0, retorno, 0, a.length);
        System.arraycopy(b, 0, retorno, a.length, b.length);
        return retorno;
    }
}
package teoriainfocodificacion;

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
        while (Math.pow(2, cantRedundante) < hamm.length) {
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
        for (int i = 1; i < tam; i++) {
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
                if (posControl != posAc && (posControl & posAc) != 0) {//Si la posicion actual debe ser controlada por el bit de control en posControl
                    arregloBits[posControl-1] ^= arregloBits[posAc - 1];//Pos control = Pos control xor bit actual
                }
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
        int cantRed =getBitsRedDeHamming(arregloBits);
        boolean[] arrInfo = new boolean[cantRed];
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
        boolean[] arrInfo = new boolean[arregloBits.length - cantRed - 1];
        boolean[] aux;
        
        aux = (arreglar) ? arreglar(arregloBits) : arregloBits.clone();
        
        int j = -1;
        for (int i = 1; i < aux.length + 1; i++) {
            if ((i & i - 1) != 0) {//Esto devuelve si no es potencia de 2
                j++;
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
    public byte[] toBytes(boolean[] arregloBits) {
        byte[] aux = new byte[(int)Math.ceil(arregloBits.length/8)];//Crea un arreglo de bytes donde van a ir los bits redondeando hacia arriba, completa con 0s
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
}
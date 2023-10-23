/* 
 * Archivo con funciones para proporsito general
 */

/**
 * Agrega funcioanlidad a un evento
 * @param {type} element Elemento al que se agrega la funcionalidad
 * @param {type} event Evento al que se agrega la funcionalidad
 * @param {type} fn Funcion que se desea agregar
 * @returns {undefined}
 */
function addEvent(element, event, fn) {
    if (element.addEventListener){
        element.addEventListener(event, fn, false);
    }
    else if (element.attachEvent){
        element.attachEvent('on' + event, fn);
    }
}


/**
 * Realiza el bloqueo del boton Back del History del Browser
 */
history.pushState({page: 1}, "title 1", "#nbb");
window.onhashchange = function(event) {
    window.location.hash = "nbb";
};


/**
 * Genera un objeto para ajax
 * @returns {XMLHttpRequest}
 */
function createAjaxObject() {
    var xmlhttp = false;
    
    try {
        xmlhttp = new ActiveXObject('Msxml2.XMLHTTP');
    } 
    catch (e) {
        try {
            xmlhttp = new ActiveXObject('Microsoft.XMLHTTP');
        } 
        catch (E) {
            xmlhttp = false;
        }
    }

    if (!xmlhttp && typeof XMLHttpRequest !== 'undefined') {
        xmlhttp = new XMLHttpRequest();
    }
    
    return xmlhttp;
}


/**
 * Habilita un parametro para modificar
 * @param {type} parameter Clave del parametro
 * @returns {undefined}
 */
function NAAdminParamsEdit(parameter){
    try{
        var caja=document.getElementById("NA:ParamValueInput:"+parameter);
        var texto=document.getElementById("NA:ParamValueText:"+parameter);
        var btnEditar=document.getElementById("NA:EditParameterButton:"+parameter);
        var btnOk=document.getElementById("NA:SaveParameterButton:"+parameter);
        var btnCancel=document.getElementById("NA:CancelParameterButton:"+parameter);

        //oculto el texto y permito la captura del valor
        if(caja!==null && btnOk!==null && btnCancel!==null){
            caja.className="NA_EditParameter_input";
            btnOk.className="NA_SaveParameter_button";
            btnCancel.className="NA_CancelParameter_button";
        }
        if(texto!==null && btnEditar!==null){
            texto.style.display="none";
            btnEditar.className="NA_null";
        }
    }catch(ex){
        alert("Error en 'NAAdminParamsEdit': "+ex.message);
    }
}


/**
 * Cancela la operacion de modificacion de un parametro
 * @param {type} parameter Clave del parametro
 * @returns {undefined}
 */
function NAAdminParamsCancel(parameter){
    try{
        var caja=document.getElementById("NA:ParamValueInput:"+parameter);
        var texto=document.getElementById("NA:ParamValueText:"+parameter);
        var btnEditar=document.getElementById("NA:EditParameterButton:"+parameter);
        var btnOk=document.getElementById("NA:SaveParameterButton:"+parameter);
        var btnCancel=document.getElementById("NA:CancelParameterButton:"+parameter);

        //oculto el texto y permito la captura del valor
        if(caja!==null && btnOk!==null && btnCancel!==null){
            caja.className="NA_null";
            btnOk.className="NA_null";
            btnCancel.className="NA_null";
        }
        if(texto!==null && btnEditar!==null){
            texto.style.display="block";
            btnEditar.className="NA_EditParameter_button";
        }
    }catch(ex){
        alert("Error en 'NAAdminParamsCancel': "+ex.message);
    }
}

/**
 * Guarda el valor de un parametro
 * @param {type} parameter Parametro a modificar
 * @param {type} session Sesion que realiza el cambio
 * @returns {undefined}
 */
function NAAdminParamsSave(parameter, session){
    try{
        var caja=document.getElementById("NA:ParamValueInput:"+parameter);
        var texto=document.getElementById("NA:ParamValueText:"+parameter);
        var ajax = createAjaxObject();
        var url="##SERVLET_CONTEXT##/neoAtlantis/resources/web/adminApp.service?NA_Operation=updateParam&NA_Session="+session+"&param="+parameter+"&value="+encodeURIComponent(caja.value);
        
        ajax.open("GET", url, true);
        ajax.onreadystatechange = function() {
            if( ajax.readyState===4 && ajax.status===200 ){
                //reviso si es un error
                if( ajax.responseText!==null && ajax.responseText.indexOf("ERROR:")!==-1 ){
                    alert("Se encontro un error: "+ajax.responseText.substring(6));
                }
                //reviso si es la respuesta
                if( ajax.responseText!==null && ajax.responseText==="DATA:true" ){
                    NAAdminParamsCancel(parameter);
                    texto.innerHTML=caja.value;
                }
            }
        };
        ajax.send();
    }catch(ex){
        alert("Error en 'NAAdminParamsSave': "+ex.message);
    }
}

function NADefaultDataChangePage(page){
    NADataChangePage(page, "NA:ChangedDataList");
}

/**
 * Cambia la pagina en una lista con paginacion
 * @param {String} page Pagina a la que se desea cambiar
 * @param {String} forma Nombre de la forma de trabajo
 * @returns {undefined}
 */
function NADataChangePage(page, forma){
    try{
        var form=document.getElementById(forma);

        if( form!==null ){
            form.NA_DataPage.value=page;
            form.submit();
        }
        else{
            alert("No existe la forma de control de paginacion: "+forma);
        }
    }
    catch(ex){
        alert("Se presento un problema en el evento 'NADataChangePage': "+ex);
    }
}

function NADefaultDataChangeOrder(order, mode){
    NADataChangeOrder(order, mode, "NA:ChangedDataList");
}

/**
 * Cambia el orden una lista con paginacion
 * @param {String} order Campo mediante al cual se debe ordenar
 * @param {String} mode Tipo de orden
 * @param {String} forma Nombre de la forma de trabajo
 * @returns {undefined}
 */
function NADataChangeOrder(order, mode, forma){
    try{
        var form=document.getElementById(forma);

        if( form!==null ){
            form.NA_DataOrder.value=order;
            form.NA_DataModeOrder.value=mode;
            form.submit();
        }
        else{
            alert("No existe la forma de control de ordenacmiento: "+forma);
        }
    }
    catch(ex){
        alert("Se presento un problema en el evento 'NADataChangeOrder': "+ex);
    }
}

/**
 * Cambia a la siguiente pagina en la lista del log
 * @returns {undefined}
 */
function NALogNextPage(){
    var form=document.getElementById("NA:ChangedDataList");
    
    if( form!==null ){
        form.NA_Operation.value="nextLog";
        form.submit();
    }
    else{
        alert("No existe la forma de control de paginacion");
    }
}

/**
 * Cambia a la pagina anterior en la lista del log
 * @returns {undefined}
 */
function NALogPreviousPage(){
    var form=document.getElementById("NA:ChangedDataList");
    
    if( form!==null ){
        form.NA_Operation.value="prevLog";
        form.submit();
    }
    else{
        alert("No existe la forma de control de paginacion");
    }
}

/**
 * Despliega el detalle de una entradadel log
 * @param {type} numberLog Id de la entrada
 * @returns {undefined}
 */
function NALogShowDetail(numberLog){
    var liga=document.getElementById("NA:LogTabView"+numberLog);
    var capa=document.getElementById("NA:LogDetailView"+numberLog);
    
    if( liga!==null && capa!==null ){
        if(liga.innerHTML==="Mostrar detalle"){
            liga.innerHTML="Ocultar detalle";
            capa.style.display="block";
        }
        else{
            liga.innerHTML="Mostrar detalle";
            capa.style.display="none";
        }
    }
    else{
        alert("No existe un evento numero: "+numberLog);
    }
}


/**
 * Actualiza la imagen del captcha
 * @returns {undefined}
 */
function NAUpdateImageCaptcha(){
    var image=document.getElementById("NA:Captcha_Image");
    var newImage= new Image();

    if( image!==null){
        newImage.src = "##SERVLET_CONTEXT##/neoAtlantis/resources/images/catpcha.jpg";
        image.src=newImage.src;
    }
}
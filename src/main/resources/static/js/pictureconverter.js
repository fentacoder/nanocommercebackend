function arrayBufferToBase64(buffer,type){
    var binary = '';
    console.log('buffer: ',buffer);
    var bytes = [].slice.call(new Uint8Array(buffer));
    bytes.forEach(b => binary += String.fromCharCode(b));
    return dataTypeFormat(window.btoa(binary),type);
}

function dataTypeFormat(convertedString = '',dataType = ''){
    dataType = dataType.toLowerCase();

    if(dataType === 'jpg' || dataType === 'jpeg'){
      return 'data:image/jpeg;base64,' + convertedString;
    }else if(dataType === 'png'){
      return 'data:image/png;base64,' + convertedString;
    }else if(dataType === 'gif'){
      return 'data:image/gif;base64,' + convertedString;
    }else{
      return '';
    }
}
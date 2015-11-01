function createAuthorForm(n){
            $form = $("<form> </form>");
            $form.append('<table>\
        <tr>\
            <td>Keyname: </td>\
            <td><input type="text" name="keyname' + n + '" id="keyname" ></td>\
        </td>\
        <tr>\
            <td>Forenames: </td>\
            <td><input type="text" name="forenames" id="forenames" ></td>\
        </td>\
        <tr>\
            <td>Affiliations: </td>\
            <td><input type="text" name="affiliations" id="affiliations" ></td>\
        </tr>\
    </table>');
         console.log($form);
}


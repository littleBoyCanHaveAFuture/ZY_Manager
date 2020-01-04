<link href="https://unpkg.com/multiple-select@1.5.2/dist/multiple-select.min.css" rel="stylesheet">

<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script src="https://unpkg.com/multiple-select@1.5.2/dist/multiple-select.min.js"></script>

<style>
    select {
        width: 100%;
    }
</style>

<div>
    <div class="form-group row">
        <label class="col-sm-2">
            Methods
        </label>

        <div class="col-sm-10">
            <button id="getOptions" class="btn btn-secondary">getOptions</button>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2">
            Basic Select
        </label>

        <div class="col-sm-10">
            <select multiple="multiple">
                <option value="text1">text1</option>
                <option value="text2">text2</option>
                <option value="text3">text3</option>
            </select>
        </div>
    </div>
    <form>
        <div class="form-group row">
            <label class="col-sm-2">
                Multiple Select
            </label>

            <div class="col-sm-10">
                <select name="select2" multiple required>
                    <option value="1">First</option>
                    <option value="2">Second</option>
                    <option value="3">Third</option>
                    <option value="4">Fourth</option>
                </select>
            </div>
        </div>

        <div class="form-group row">
            <div class="col-sm-10 offset-sm-2">
                <button type="submit" class="btn btn-primary">Submit</button>
            </div>
        </div>
    </form>
</div>

<script>
    var $select = $('select')

    $(function () {
        $select.multipleSelect({
            filter: true
        })

        $('#getOptions').click(function () {
            console.log($select.multipleSelect('getOptions'))
            alert(JSON.stringify($select.multipleSelect('getOptions'), null, 4))
        });
        $('form').submit(function () {
            alert($(this).serialize())
            return false
        })

    })
</script>
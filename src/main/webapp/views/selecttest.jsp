<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Hello, Multiple Select!</title>

    <%--    <link rel="stylesheet" href="https://unpkg.com/multiple-select@1.5.2/dist/multiple-select.min.css">--%>
    <%--    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>--%>
    <%--    <script src="https://unpkg.com/multiple-select@1.5.2/dist/multiple-select.min.js"></script>--%>


    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/jquery-easyui-1.3.3/themes/icon.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/common.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/multiple-select-1.5.2/multiple-select.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/userManager.js"></script>

    <style>
        select {
            width: 50%;
        }
    </style>
</head>
<body>
<!-- Single Select -->
<label>
    <select>
        <option value="1">January</option>
        <option value="2">February</option>
        <option value="3">March</option>
        <option value="4">April</option>
        <option value="5">May</option>
        <option value="6">June</option>
        <option value="7">July</option>
        <option value="8">August</option>
        <option value="9">September</option>
        <option value="10">October</option>
        <option value="11">November</option>
        <option value="12">December</option>
    </select>
</label>

<!-- Multiple Select -->
<label>
    <select multiple="multiple">
        <option value="1">January</option>
        <option value="2">February</option>
        <option value="3">March</option>
        <option value="4">April</option>
        <option value="5">May</option>
        <option value="6">June</option>
        <option value="7">July</option>
        <option value="8">August</option>
        <option value="9">September</option>
        <option value="10">October</option>
        <option value="11">November</option>
        <option value="12">December</option>
    </select>
</label>
<div class="form-group row">
    <label class="col-sm-2">
        Multiple Select
    </label>

    <div class="col-sm-10">
        <select multiple="multiple" class="multiple-select">
            <optgroup label="Group 1">
                <option value="1">Option 1</option>
                <option value="2">Option 2</option>
                <option value="3">Option 3</option>
            </optgroup>
            <optgroup label="Group 2">
                <option value="4">Option 4</option>
                <option value="5">Option 5</option>
                <option value="6">Option 6</option>
            </optgroup>
            <optgroup label="Group 3">
                <option value="7">Option 7</option>
                <option value="8">Option 8</option>
                <option value="9">Option 9</option>
            </optgroup>
        </select>
    </div>
</div>
<div class="col-sm-10">
    <label class="qqq">
        模块权限：
    </label>
    <select title="选择模块" multiple="multiple" name="ddd" size="10" id="qqq">
        <%--    <select multiple="multiple" class="multiple-select" id="qqq">--%>
        <tr></tr>
    </select>
</div>

<script type="text/javascript">
    $(function () {
        // 初始化内容 先加载完列表
        $.ajax({
            //获取下拉
            url: "/users/getFuncList",
            type: "post",
            async: false,
            data: {},
            dataType: "json",
            success: function (combox) {//动态设置玩家渠道下拉列表
                var result = combox;
                //数据是有序的
                var times = 20;//大模块个数
                var start = 10000;
                var max = result[result.length - 1].id;//最大的数
                console.log("[max]----->" + max);

                for (var int = 1; int <= times; int++) {
                    var end = start + 10000;
                    console.log("[]----->" + start + ":" + end);
                    var id = new Array();
                    var name = new Array();
                    var num = 0;
                    if (start > max) {
                        return;
                    }
                    for (var i = 0; i < result.length; i++) {
                        var res = parseInt(result[i].id / 10000);
                        if (res === int) {
                            id[num] = result[i].id;
                            name[num] = result[i].name;
                            num++;
                        }
                    }
                    console.log(id);
                    console.log(name);
                    $("#qqq").append("<optgroup label='" + name[0] + "'>");
                    for (var i = 1; i < num; i++) {
                        $("#qqq").append("<option value='" + id[i] + "'>" + name[i] + "</option>");
                    }
                    $("#qqq").append("</optgroup>");
                    start = end;
                }
            }
        });
        $('select').multipleSelect()
        $("#qqq").multipleSelect({
            placeholder: "请选择",
            width: 250,
            multiple: true,
            multipleWidth: 150,
        });
    })
</script>

</body>
</html>
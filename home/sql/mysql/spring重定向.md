如果逻辑视图名是这样的形式：redirect:/myapp/some/resource，他们重定向路径将以Servlet上下文作为相对路径进行查找，而逻辑视图名如果是这样的形式：redirect:http://myhost.com/some/arbitrary/path，那么重定向URL使用的就是绝对路径。

```java
@Controller
@RequestMapping("/rfqPreauditSupplier")
public class RfqPreauditSupplierController extends BaseController{

  @RequestMapping("/preauditSupplierEdit")
  public ModelAndView preauditSupplierEdit(HttpServletRequest request, HttpServletResponse response, RfqRequestBo bo) {

    ModelAndView mav = new ModelAndView();
      String url = "rfqQuotation/init";
      mav.setViewName("redirect:"+url);//这样重定向后的url为/rfqPreauditSupplier/rfqQuotation/init
      //所以可以使用绝对路径
      /**
      String path = request.getRequestURL().toString(); //request.getServletPath();
            String url = path.replace("rfqPreauditSupplier/preauditSupplierEdit","rfqQuotation/init");
            mav.setViewName("redirect:"+url);
      */
      return mav;
  }

    //这样可以重定向到不同的controller中
    @RequestMapping(value = "/tbqChooseItemOrMoney",method = RequestMethod.GET)
    public String tbqChooseItemOrMoney(String requestId) {

      
        RfqRulesVo rfqRulesVo = tbqRulesService.rfqRulesByRequestId(requestId);
        String url = "";
        if("1".equals(rfqRulesVo.getBiddingMethod())){ //按总价
            url=  "redirect:/dacDesignateContract/init?id=" + requestId;
        }else if ("0".equals(rfqRulesVo.getBiddingMethod())){  //按物料
             url = "redirect:/rfqQuotationNotice/init?requestId=" + requestId;
        }
        return url;
    }

}
```


注意的是，如果控制器方法注解了@ResponseStatus，那么注解设置的状态码值会覆盖RedirectView设置的响应状态码值。

# Bean

Bean的作用域

* singleton：单例，指一个Bean容器中只存在一份
* prototype：每次请求（每次使用）创建新的实例，destroy方式不生效
* request：每次http请求创建一个实例且仅在当前request内有效
* session：同上，每次http请求创建，当前session内有效
* global session：基于portlet的web中有效（portlet定义了global session），如果是在web中，同session

Bean的自动装配（Autowiring）

* No：不做任何操作
* byname：根据属性名自动装配。此选项将检查容器并根据名字查找与属性完全一致的bean，并将其与属性自动装配
* byType：如果容器中存在一个与指定属性类型相同的bean，那么将与该属性自动装配；如果存在多个该类型bean，那么抛出异常，并指出不能使用byType方式进行自动装配；如果没有找到相匹配的bean，则什么事都不发生
* Constructor：与byType方式类似，不同之处在于它应用于构造器参数。如果容器中没有找到与构造器参数类型一致的bean，那么抛出异常
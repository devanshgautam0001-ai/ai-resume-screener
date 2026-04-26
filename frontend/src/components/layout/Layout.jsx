import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";

export default function Layout() {
  return (
    <div className="page-shell min-h-screen p-4 md:p-6">
      <div className="mx-auto grid max-w-7xl gap-6 lg:grid-cols-[280px_1fr]">
        <Sidebar />
        <div className="space-y-6">
          <Navbar />
          <main>
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
